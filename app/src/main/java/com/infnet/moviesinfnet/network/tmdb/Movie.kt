package com.infnet.moviesinfnet.network.tmdb


import com.infnet.moviesinfnet.model.LocalMovie
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class MovieTmdb(
    @Json(name = "backdrop_path")
    val backdropPath: String?,
    val id: Int?,
    val overview: String?,
    val popularity: Double?,
    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "release_date")
    val releaseDate: String?,
    val title: String?,
    @Json(name = "vote_average")
    val voteAverage: Double?,
    @Json(name = "vote_count")
    val voteCount: Int?
)
{
    private val canMapToLocalMovie: Boolean
        get() = id != null && overview != null && title != null

    val toLocalMovie: LocalMovie?
        get()
        {
            return if (canMapToLocalMovie)
            {
                LocalMovie(
                    id = id!!.toString(),
                    title = title!!,
                    overview = overview!!,
                    backdropPath = backdropPath,
                    posterPath = posterPath,
                    popularity = popularity,
                    releaseDate = releaseDate,
                    voteAverage = voteAverage,
                    voteCount = voteCount,
                    isWatched = null
                )
            }
            else
            {
                null
            }
        }
}