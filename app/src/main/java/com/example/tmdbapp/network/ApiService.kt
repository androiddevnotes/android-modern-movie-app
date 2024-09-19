package com.example.tmdbapp.network

import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.models.MovieResponse
import kotlinx.serialization.Serializable
import retrofit2.http.*

interface ApiService {
  @GET("movie/popular")
  suspend fun getPopularMovies(
    @Query("api_key") apiKey: String,
    @Query("page") page: Int,
  ): MovieResponse

  @GET("movie/now_playing")
  suspend fun getNowPlayingMovies(
    @Query("api_key") apiKey: String,
    @Query("page") page: Int,
  ): MovieResponse

  @GET("movie/top_rated")
  suspend fun getTopRatedMovies(
    @Query("api_key") apiKey: String,
    @Query("page") page: Int,
  ): MovieResponse

  @GET("movie/upcoming")
  suspend fun getUpcomingMovies(
    @Query("api_key") apiKey: String,
    @Query("page") page: Int,
  ): MovieResponse

  @GET("discover/movie")
  suspend fun discoverMovies(
    @Query("api_key") apiKey: String,
    @Query("page") page: Int,
    @Query("sort_by") sortBy: String? = null,
    @Query("with_genres") genres: String? = null,
    @Query("primary_release_year") releaseYear: Int? = null,
    @Query("vote_average.gte") minRating: Float? = null,
  ): MovieResponse

  @GET("search/movie")
  suspend fun searchMovies(
    @Query("api_key") apiKey: String,
    @Query("query") query: String,
    @Query("page") page: Int,
  ): MovieResponse

  @GET("movie/{movie_id}")
  suspend fun getMovieDetails(
    @Path("movie_id") movieId: Int,
    @Query("api_key") apiKey: String,
  ): Movie

  // soemthing

  @GET("authentication/token/new")
  suspend fun createRequestToken(
    @Query("api_key") apiKey: String,
  ): RequestTokenResponse

  @POST("authentication/session/new")
  suspend fun createSession(
    @Query("api_key") apiKey: String,
    @Body requestBody: CreateSessionRequest,
  ): CreateSessionResponse

  @POST("list")
  suspend fun createList(
    @Query("api_key") apiKey: String,
    @Query("session_id") sessionId: String,
    @Body requestBody: CreateListRequest,
  ): CreateListResponse
}

@Serializable
data class RequestTokenResponse(
  val success: Boolean,
  val expires_at: String,
  val request_token: String,
)

@Serializable
data class CreateSessionRequest(
  val requestToken: String,
)

@Serializable
data class CreateSessionResponse(
  val success: Boolean,
  val session_id: String,
)

@Serializable
data class CreateListRequest(
  val name: String,
  val description: String,
  val language: String = "en",
)

@Serializable
data class CreateListResponse(
  val status_message: String,
  val success: Boolean,
  val status_code: Int,
  val list_id: Int,
)
