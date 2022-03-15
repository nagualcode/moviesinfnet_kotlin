package com.infnet.moviesinfnet.network.firebase

import com.infnet.moviesinfnet.model.LocalMovie


data class FirebaseMovie(
    val backdropPath: String? = null,
    val watched: Boolean? = null,
    val overview: String = "",
    val popularity: Double? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val title: String = "",
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
)

fun Map.Entry<String, FirebaseMovie>.toLocalMovie() =
        LocalMovie(
            id = key,
            title = value.title,
            overview = value.overview,
            backdropPath = value.backdropPath,
            posterPath = value.posterPath,
            popularity = value.popularity,
            releaseDate = value.releaseDate,
            voteAverage = value.voteAverage,
            voteCount = value.voteCount,
            isWatched = value.watched,
        )