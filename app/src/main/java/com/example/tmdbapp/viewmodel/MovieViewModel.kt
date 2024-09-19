package com.example.tmdbapp.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.tmdbapp.R
import com.example.tmdbapp.data.SessionManager
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.repository.MovieRepository
import com.example.tmdbapp.utils.Constants
import com.example.tmdbapp.utils.MovieError
import com.example.tmdbapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MovieViewModel(
  application: Application,
) : AndroidViewModel(application) {
  private var currentPage = 1
  private var isLoading = false
  private var searchJob: Job? = null
  private val _currentMovie = MutableStateFlow<Movie?>(null)
  private val _currentSortOption = MutableStateFlow(SortOption.POPULAR)
  private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
  private val _filterOptions = MutableStateFlow(FilterOptions())
  private val _lastViewedItemIndex = MutableStateFlow(0)
  private val _searchQuery = MutableStateFlow("")
  private val _selectedMovie = MutableStateFlow<Movie?>(null)
  private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
  private val repository = MovieRepository(application)
  internal var isLastPage = false

  val currentMovie: StateFlow<Movie?> = _currentMovie
  val currentSortOption: StateFlow<SortOption> = _currentSortOption
  val favorites: StateFlow<List<Movie>> = _favorites
  val filterOptions: StateFlow<FilterOptions> = _filterOptions
  val lastViewedItemIndex: StateFlow<Int> = _lastViewedItemIndex
  val searchQuery: StateFlow<String> = _searchQuery
  val uiState: StateFlow<MovieUiState> = _uiState

  private val sessionManager = SessionManager(application)

  private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
  val authState: StateFlow<AuthState> = _authState

  private val _createListState = MutableStateFlow<CreateListState>(CreateListState.Idle)
  val createListState: StateFlow<CreateListState> = _createListState

  init {
    fetchPopularMovies()
    loadFavorites()
    checkAuthenticationStatus()
  }

  fun downloadImage(
    posterPath: String?,
    context: Context,
  ) {
    viewModelScope.launch {
      if (posterPath == null) {
        Toast
          .makeText(
            context,
            context.getString(R.string.error_no_image),
            Toast.LENGTH_SHORT,
          ).show()
        return@launch
      }

      val imageUrl = "${Constants.BASE_IMAGE_URL}$posterPath"

      try {
        val bitmap =
          withContext(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request =
              ImageRequest
                .Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            (result as BitmapDrawable).bitmap
          }

        val filename = "TMDB_${System.currentTimeMillis()}.jpg"
        val contentValues =
          ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
              put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
              put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
          }

        val resolver = context.contentResolver
        val uri =
          resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
          withContext(Dispatchers.IO) {
            resolver.openOutputStream(it)?.use { outputStream ->
              bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
          }

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(it, contentValues, null, null)
          }

          Toast
            .makeText(
              context,
              context.getString(R.string.success_image_saved),
              Toast.LENGTH_SHORT,
            ).show()
        } ?: run {
          Toast
            .makeText(
              context,
              context.getString(R.string.error_failed_to_save),
              Toast.LENGTH_SHORT,
            ).show()
        }
      } catch (e: IOException) {
        Toast
          .makeText(
            context,
            "${context.getString(R.string.error_download_failed)}: ${e.localizedMessage}",
            Toast.LENGTH_SHORT,
          ).show()
      }
    }
  }

  fun fetchMovieDetails(movieId: Int) {
    viewModelScope.launch {
      try {
        val movie = repository.getMovieDetails(movieId)
        _currentMovie.value = movie
      } catch (e: Exception) {
        _currentMovie.value = null
      }
    }
  }

  fun loadMoreMovies() {
    fetchMovies()
  }

  fun refreshMovies() {
    currentPage = 1
    isLastPage = false
    _uiState.value = MovieUiState.Loading
    fetchMovies()
  }

  fun setFilterOptions(options: FilterOptions) {
    _filterOptions.value = options
    currentPage = 1
    isLastPage = false
    _uiState.value = MovieUiState.Loading
    fetchMovies()
  }

  fun setLastViewedItemIndex(index: Int) {
    _lastViewedItemIndex.value = index
  }

  fun setSearchQuery(query: String) {
    val oldQuery = _searchQuery.value
    _searchQuery.value = query
    searchJob?.cancel()
    if (query.isNotEmpty()) {
      searchJob =
        viewModelScope.launch {
          delay(Constants.DELAY_SEARCH)
          searchMovies(query)
        }
    } else if (oldQuery.isNotEmpty() && query.isEmpty()) {
      refreshMovies()
    }
  }

  fun setSortOption(sortOption: SortOption) {
    if (_currentSortOption.value != sortOption) {
      _currentSortOption.value = sortOption
      currentPage = 1
      isLastPage = false
      _uiState.value = MovieUiState.Loading
      fetchMovies()
    }
  }

  fun toggleFavorite(movie: Movie) {
    viewModelScope.launch {
      repository.toggleFavorite(movie)
      val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)

      _currentMovie.value = updatedMovie

      when (val currentState = _uiState.value) {
        is MovieUiState.Success -> {
          val updatedMovies =
            currentState.movies.map {
              if (it.id == movie.id) updatedMovie else it
            }
          _uiState.value = MovieUiState.Success(updatedMovies)
        }

        else -> {}
      }

      _selectedMovie.update { current ->
        if (current?.id == movie.id) updatedMovie else current
      }

      if (updatedMovie.isFavorite) {
        _favorites.update { it + updatedMovie }
      } else {
        _favorites.update { it.filter { m -> m.id != updatedMovie.id } }
      }
    }
  }

  private fun fetchMovies() {
    if (isLoading || isLastPage) return
    isLoading = true
    viewModelScope.launch {
      try {
        val result =
          repository.discoverMovies(
            page = currentPage,
            sortBy = _currentSortOption.value.apiValue,
            genres = _filterOptions.value.genres,
            releaseYear = _filterOptions.value.releaseYear,
            minRating = _filterOptions.value.minRating,
          )
        when (result) {
          is Resource.Success -> {
            val newMovies = result.data?.results ?: emptyList()
            val currentMovies =
              if (_uiState.value is MovieUiState.Success && currentPage > 1) {
                (_uiState.value as MovieUiState.Success).movies
              } else {
                emptyList()
              }
            _uiState.value = MovieUiState.Success(currentMovies + newMovies)
            currentPage++
            isLastPage = newMovies.isEmpty()
          }

          is Resource.Error -> {
            _uiState.value = MovieUiState.Error(handleError(result.message))
          }
        }
      } catch (e: Exception) {
        _uiState.value = MovieUiState.Error(handleError(e))
      } finally {
        isLoading = false
      }
    }
  }

  private fun fetchPopularMovies() {
    if (isLoading || isLastPage) return
    isLoading = true
    viewModelScope.launch {
      try {
        val result = repository.getPopularMovies(currentPage)
        when (result) {
          is Resource.Success -> {
            val newMovies = result.data?.results ?: emptyList()
            val currentMovies =
              if (_uiState.value is MovieUiState.Success) {
                (_uiState.value as MovieUiState.Success).movies
              } else {
                emptyList()
              }
            _uiState.value = MovieUiState.Success(currentMovies + newMovies)
            currentPage++
            isLastPage = newMovies.isEmpty()
          }

          is Resource.Error -> {
            _uiState.value = MovieUiState.Error(handleError(result.message))
          }
        }
      } catch (e: Exception) {
        _uiState.value = MovieUiState.Error(handleError(e))
      } finally {
        isLoading = false
      }
    }
  }

  private fun handleError(errorMessage: String?): MovieError = MovieError.ApiError(errorMessage ?: "An unknown error occurred")

  private fun handleError(error: Throwable): MovieError =
    when (error) {
      is IOException -> MovieError.Network
      is HttpException -> {
        if (error.code() in 500..599) {
          MovieError.Server
        } else {
          MovieError.ApiError(error.message())
        }
      }

      else -> MovieError.Unknown
    }

  private fun loadFavorites() {
    viewModelScope.launch {
      val favoriteMovies = repository.getFavoriteMovies()
      _favorites.value = favoriteMovies
    }
  }

  private fun searchMovies(query: String) {
    viewModelScope.launch {
      _uiState.value = MovieUiState.Loading
      when (val result = repository.searchMovies(query, 1)) {
        is Resource.Success -> {
          _uiState.value = MovieUiState.Success(result.data?.results ?: emptyList())
        }

        is Resource.Error -> {
          _uiState.value = MovieUiState.Error(handleError(result.message))
        }
      }
    }
  }

  fun isFavorite(movieId: Int): Boolean = favorites.value.any { it.id == movieId }

  fun startAuthentication() {
    viewModelScope.launch {
      if (_authState.value == AuthState.Authenticated) return@launch

      _authState.value = AuthState.Loading
      when (val tokenResult = repository.createRequestToken()) {
        is Resource.Success -> {
          val token = tokenResult.data
          if (token != null) {
            _authState.value = AuthState.RequestTokenCreated(token)
          } else {
            _authState.value = AuthState.Error("Failed to create request token")
          }
        }
        is Resource.Error -> _authState.value = AuthState.Error(tokenResult.message ?: "Unknown error")
      }
    }
  }

  fun createSession(approvedToken: String) {
    viewModelScope.launch {
      _authState.value = AuthState.Loading
      when (val sessionResult = repository.createSession(approvedToken)) {
        is Resource.Success -> _authState.value = AuthState.Authenticated
        is Resource.Error -> _authState.value = AuthState.Error(sessionResult.message ?: "Failed to create session")
      }
    }
  }

  fun createList(
    name: String,
    description: String,
  ) {
    viewModelScope.launch {
      _createListState.value = CreateListState.Loading
      when (val result = repository.createList(name, description)) {
        is Resource.Success ->
          _createListState.value =
            result.data?.let {
              CreateListState.Success(
                it,
              )
            }!!
        is Resource.Error ->
          _createListState.value =
            result.message?.let {
              CreateListState.Error(
                it,
              )
            }!!
      }
    }
  }

  private fun checkAuthenticationStatus() {
    viewModelScope.launch {
      val sessionId = sessionManager.getSessionId()
      if (sessionId != null) {
        _authState.value = AuthState.Authenticated
      }
    }
  }
}
