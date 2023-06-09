package fr.epf.min.movieappepf

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {
    @GET("search/movie")
    fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): Call<ModelSearch>

    @GET("movie/{id}/recommendations")
    fun getRecommendations(
        @Path("id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Call<ModelSearch>
    @GET("movie/{id}")
    fun getMovieDetails(
        @Path("id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Call<Movie>
    @GET("trending/movie/day")
    fun getTrendingMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Call<ModelSearch>

}

