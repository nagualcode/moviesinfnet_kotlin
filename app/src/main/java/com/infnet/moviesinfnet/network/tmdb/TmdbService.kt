package com.infnet.moviesinfnet.network.tmdb

import com.infnet.moviesinfnet.utils.const.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbService
{

    @GET("search/movie?language=en-US&api_key=${API_KEY}")
    suspend fun getMovieFromTitle(
        @Query("query") title: String,
    ): TmdbResponse


    @GET("trending/movie/week?api_key=${API_KEY}")
    suspend fun getTrendingMovies(): TmdbResponse
}