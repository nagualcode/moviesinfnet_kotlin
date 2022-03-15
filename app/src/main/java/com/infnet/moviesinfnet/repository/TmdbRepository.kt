package com.infnet.moviesinfnet.repository

import com.infnet.moviesinfnet.model.LocalMovie
import com.infnet.moviesinfnet.network.tmdb.TmdbService
import com.infnet.moviesinfnet.utils.ext.makeEvent
import com.infnet.moviesinfnet.utils.status.BaseStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class TmdbRepository @Inject constructor(
    private val tmdbService: TmdbService
)
{
    fun getMoviesByTitle(title: String): Flow<BaseStatus<List<LocalMovie>>> =
            flow {
                emit(BaseStatus.Loading)
                try
                {
                    val tmdbResponse = tmdbService.getMovieFromTitle(title)
                    emit(
                        BaseStatus.Success(
                            tmdbResponse.movies.mapNotNull {
                                it.toLocalMovie
                            }
                        )
                    )
                }
                catch (e: Exception)
                {
                    emit(BaseStatus.Failed(e.makeEvent))
                }
            }


    fun getTrending(): Flow<BaseStatus<List<LocalMovie>>> =
            flow {
                emit(BaseStatus.Loading)
                try
                {
                    val tmdbResponse = tmdbService.getTrendingMovies()
                    emit(
                        BaseStatus.Success(
                            tmdbResponse.movies.mapNotNull {
                                it.toLocalMovie
                            }
                        )
                    )
                }
                catch (e: Exception)
                {
                    emit(BaseStatus.Failed(e.makeEvent))
                }
            }

}