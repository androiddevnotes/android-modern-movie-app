package com.example.tmdbapp.repository

import com.example.tmdbapp.data.FavoritePreferencesDatastore
import com.example.tmdbapp.data.SessionManagerPreferencesDataStore
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.network.TmdbApiService
import com.example.tmdbapp.network.responses.tmdb.*
import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.Resource
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RepositoryTest {
    private lateinit var repository: Repository
    private val mockContext = mockk<android.content.Context>(relaxed = true)
    private val mockTmdbApi = mockk<TmdbApiService>()
    private val mockFavoriteDatastore = mockk<FavoritePreferencesDatastore>()
    private val mockSessionManager = mockk<SessionManagerPreferencesDataStore>()
    private val mockApiKeyManager = mockk<ApiKeyManager>()

    private val testMovie1 = Movie(
        id = 1,
        title = "Movie 1",
        overview = "Overview 1",
        posterPath = "path1",
        voteAverage = 8.0f,
        releaseDate = "2023-01-01"
    )

    private val testMovie2 = Movie(
        id = 2,
        title = "Movie 2",
        overview = "Overview 2",
        posterPath = "path2",
        voteAverage = 7.5f,
        releaseDate = "2023-02-01"
    )

    @Before
    fun setup() {
        repository = Repository(mockContext)
        repository.tmdbApi = mockTmdbApi
        repository.favoritePreferencesDatastore = mockFavoriteDatastore
        repository.sessionManagerPreferencesDataStore = mockSessionManager
        repository.apiKeyManager = mockApiKeyManager

        // Setup default mock behaviors
        every { mockApiKeyManager.tmdbApiKeyFlow } returns flowOf("test_api_key")
        every { mockApiKeyManager.openAiApiKeyFlow } returns flowOf("test_openai_key")
    }

    @Test
    fun `safeApiCall returns Success when API call succeeds`() = runTest {
        // Given
        val expectedResult = "Test Success"
        val apiCall: suspend () -> String = { expectedResult }

        // When
        val result = repository.safeApiCall(apiCall)

        // Then
        assertTrue(result is Resource.Success<String>)
        assertEquals(expectedResult, (result as Resource.Success<String>).data)
    }

    @Test
    fun `safeApiCall returns Error when API call throws exception`() = runTest {
        // Given
        val errorMessage = "Test Error"
        val apiCall: suspend () -> String = { throw Exception(errorMessage) }

        // When
        val result = repository.safeApiCall(apiCall)

        // Then
        assertTrue(result is Resource.Error<String>)
        assertEquals(errorMessage, (result as Resource.Error<String>).message)
    }

    @Test
    fun `safeApiCall returns Error with default message when exception has no message`() = runTest {
        // Given
        val apiCall: suspend () -> String = { throw Exception() }

        // When
        val result = repository.safeApiCall(apiCall)

        // Then
        assertTrue(result is Resource.Error<String>)
        assertEquals("An unexpected error occurred", (result as Resource.Error<String>).message)
    }

    @Test
    fun `safeApiCall works with different types`() = runTest {
        // Given
        val expectedResult = 42
        val apiCall: suspend () -> Int = { expectedResult }

        // When
        val result = repository.safeApiCall(apiCall)

        // Then
        assertTrue(result is Resource.Success<Int>)
        assertEquals(expectedResult, (result as Resource.Success<Int>).data)
    }

    @Test
    fun `safeApiCall works with custom data class`() = runTest {
        // Given
        data class TestData(val id: Int, val name: String)
        val expectedResult = TestData(1, "Test")
        val apiCall: suspend () -> TestData = { expectedResult }

        // When
        val result = repository.safeApiCall(apiCall)

        // Then
        assertTrue(result is Resource.Success<TestData>)
        assertEquals(expectedResult, (result as Resource.Success<TestData>).data)
    }

    @Test
    fun `toggleFavorite updates favorite status`() = runTest {
        // Given
        val movie = testMovie1.copy(isFavorite = false)
        coEvery { mockFavoriteDatastore.setFavorite(any(), any()) } just Runs

        // When
        repository.toggleFavorite(movie)

        // Then
        coVerify { mockFavoriteDatastore.setFavorite(1, true) }
    }

    @Test
    fun `getFavoriteMovies returns list of favorite movies`() = runTest {
        // Given
        val favoriteIds = setOf(1, 2)
        
        every { mockFavoriteDatastore.getAllFavorites() } returns flowOf(favoriteIds)
        coEvery { mockTmdbApi.getMovieDetails(eq(1), any()) } returns testMovie1
        coEvery { mockTmdbApi.getMovieDetails(eq(2), any()) } returns testMovie2
        every { mockFavoriteDatastore.isFavorite(eq(1)) } returns flowOf(true)
        every { mockFavoriteDatastore.isFavorite(eq(2)) } returns flowOf(true)

        // When
        val result = repository.getFavoriteMovies().first()

        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.id == 1 && it.isFavorite })
        assertTrue(result.any { it.id == 2 && it.isFavorite })
    }

    @Test
    fun `searchMovies returns movies with favorite status`() = runTest {
        // Given
        val query = "test"
        val page = 1
        val movieResponse = MovieResponse(
            page = 1,
            results = listOf(testMovie1, testMovie2),
            totalPages = 1,
            totalResults = 2
        )
        
        coEvery { mockTmdbApi.searchMovies(any(), any(), any()) } returns movieResponse
        every { mockFavoriteDatastore.getAllFavorites() } returns flowOf(setOf(1))

        // When
        val result = repository.searchMovies(query, page)

        // Then
        assertTrue(result is Resource.Success)
        val response = (result as Resource.Success<MovieResponse>).data
        assertNotNull(response)
        assertEquals(2, response.results.size)
        assertTrue(response.results[0].isFavorite) // First movie should be favorite
        assertTrue(!response.results[1].isFavorite) // Second movie should not be favorite
    }

    @Test
    fun `getMovieDetails returns movie with favorite status`() = runTest {
        // Given
        val movieId = 1
        
        coEvery { mockTmdbApi.getMovieDetails(any(), any()) } returns testMovie1
        every { mockFavoriteDatastore.isFavorite(any()) } returns flowOf(true)

        // When
        val result = repository.getMovieDetails(movieId)

        // Then
        assertTrue(result is Resource.Success)
        val returnedMovie = (result as Resource.Success<Movie>).data
        assertNotNull(returnedMovie)
        assertEquals(movieId, returnedMovie.id)
        assertTrue(returnedMovie.isFavorite)
    }

    @Test
    fun `createSession saves session ID on success`() = runTest {
        // Given
        val approvedToken = "test_token"
        val sessionId = "test_session_id"
        val response = CreateSessionResponse(success = true, sessionId = sessionId)
        
        coEvery { mockTmdbApi.createSession(any(), any()) } returns response
        coEvery { mockSessionManager.saveSessionId(any()) } just Runs

        // When
        val result = repository.createSession(approvedToken)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(sessionId, (result as Resource.Success<String>).data)
        coVerify { mockSessionManager.saveSessionId(sessionId) }
    }

    @Test
    fun `createSession returns error when request fails`() = runTest {
        // Given
        val approvedToken = "test_token"
        val response = CreateSessionResponse(success = false, sessionId = "")
        
        coEvery { mockTmdbApi.createSession(any(), any()) } returns response

        // When
        val result = repository.createSession(approvedToken)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("Failed to create session", (result as Resource.Error<String>).message)
    }
} 