package com.infnet.moviesinfnet.utils.const

import com.google.firebase.database.GenericTypeIndicator
import com.infnet.moviesinfnet.network.firebase.FirebaseMovie


object DatabaseFields
{
    const val USERS_MOVIE_DATA = "UsersMovieData"

    const val MOVIE_TITLE_FIELD = "title"
    const val MOVIE_OVERVIEW_FIELD = "overview"
    const val MOVIE_BACKDROP_PATH_FIELD = "backdropPath"
    const val MOVIE_POSTER_PATH_FIELD = "posterPath"
    const val MOVIE_POPULARITY_FIELD = "popularity"
    const val MOVIE_RELEASE_DATE_FIELD = "releaseDate"
    const val MOVIE_VOTE_AVERAGE_FIELD = "voteAverage"
    const val MOVIE_VOTE_COUNT_FIELD = "voteCount"
    const val MOVIE_IS_WATCHED_FIELD = "watched"

    val FirebaseMovieInstance = object : GenericTypeIndicator<Map<String, FirebaseMovie>>()
    {

    }

}