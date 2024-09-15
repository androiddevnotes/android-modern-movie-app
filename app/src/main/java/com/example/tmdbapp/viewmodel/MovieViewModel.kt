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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

sealed class MovieUiState {
    object Loading : MovieUiState()
    data class Success(val movies: List<Movie>) : MovieUiState()
    data class Error(val error: MovieError) : MovieUiState()
}

data class ListScrollPosition(
    val firstVisibleItemIndex: Int,
    val firstVisibleItemScrollOffset: Int
)

data class ScrollPosition(val firstVisibleItemIndex: Int, val firstVisibleItemScrollOffset: Int)

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MovieRepository(application)

    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState: StateFlow<MovieUiState> = _uiState

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie

    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    val favorites: StateFlow<List<Movie>> = _favorites

    private var currentPage = 1
    internal var isLastPage = false
    private var isLoading = false

    private val _currentMovie = MutableStateFlow<Movie?>(null)
    val currentMovie: StateFlow<Movie?> = _currentMovie

    private val _currentSortOption = MutableStateFlow(SortOption.POPULAR)
    val currentSortOption: StateFlow<SortOption> = _currentSortOption

    private val _filterOptions = MutableStateFlow(FilterOptions())
    val filterOptions: StateFlow<FilterOptions> = _filterOptions

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var searchJob: Job? = null

    init {
        fetchPopularMovies()
        loadFavorites()
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
                        val currentMovies = if (_uiState.value is MovieUiState.Success) {
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

    private fun handleError(error: Throwable): MovieError {
        return when (error) {
            is IOException -> MovieError.Network
            is HttpException -> {
                when (error.code()) {
                    in 400..499 -> MovieError.ApiError("API Error: ${error.message()}")
                    in 500..599 -> MovieError.Server
                    else -> MovieError.Unknown
                }
            }

            else -> MovieError.Unknown
        }
    }

    private fun handleError(errorMessage: String?): MovieError {
        return MovieError.ApiError(errorMessage ?: "An unknown error occurred")
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            val favoriteMovies = repository.getFavoriteMovies()
            _favorites.value = favoriteMovies
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

    fun setFilterOptions(options: FilterOptions) {
        _filterOptions.value = options
        currentPage = 1
        isLastPage = false
        _uiState.value = MovieUiState.Loading
        fetchMovies()
    }

    private fun fetchMovies() {
        if (isLoading || isLastPage) return
        isLoading = true
        viewModelScope.launch {
            try {
                val result = repository.discoverMovies(
                    page = currentPage,
                    sortBy = _currentSortOption.value.apiValue,
                    genres = _filterOptions.value.genres,
                    releaseYear = _filterOptions.value.releaseYear,
                    minRating = _filterOptions.value.minRating
                )
                when (result) {
                    is Resource.Success -> {
                        val newMovies = result.data?.results ?: emptyList()
                        val currentMovies = if (_uiState.value is MovieUiState.Success && currentPage > 1) {
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

    fun loadMoreMovies() {
        fetchMovies()
    }

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
        _currentMovie.value = movie
    }

    fun clearSelectedMovie() {
        _selectedMovie.value = null
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            repository.toggleFavorite(movie)
            val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)

            // Update the current movie
            _currentMovie.value = updatedMovie

            when (val currentState = _uiState.value) {
                is MovieUiState.Success -> {
                    val updatedMovies = currentState.movies.map {
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

    fun getMovieById(movieId: Int): Movie? {
        return when (val currentState = _uiState.value) {
            is MovieUiState.Success -> currentState.movies.find { it.id == movieId }
            else -> null
        }
    }

    fun refreshMovies() {
        currentPage = 1
        isLastPage = false
        _uiState.value = MovieUiState.Loading
        fetchMovies()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.isNotEmpty()) {
            searchJob = viewModelScope.launch {
                delay(300) // Debounce for 300ms
                searchMovies(query)
            }
        } else {
            refreshMovies()
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

    fun downloadImage(posterPath: String?, context: Context) {
        viewModelScope.launch {
            if (posterPath == null) {
                Toast.makeText(context, "No image available to download", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val imageUrl = "${Constants.BASE_IMAGE_URL}$posterPath"
            
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    val loader = ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .allowHardware(false)
                        .build()

                    val result = (loader.execute(request) as SuccessResult).drawable
                    (result as BitmapDrawable).bitmap
                }

                val filename = "TMDB_${System.currentTimeMillis()}.jpg"
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

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

                    Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                } ?: run {
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Toast.makeText(context, "Failed to save image: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

data class FilterOptions(
    val genres: List<Int> = emptyList(),
    val releaseYear: Int? = null,
    val minRating: Float? = null
)

enum class SortOption(val apiValue: String) {
    POPULAR("popularity.desc"),
    NOW_PLAYING("release_date.desc"),
    TOP_RATED("vote_average.desc"),
    UPCOMING("primary_release_date.asc")
}
