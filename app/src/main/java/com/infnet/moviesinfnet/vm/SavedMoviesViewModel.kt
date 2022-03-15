package com.infnet.moviesinfnet.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infnet.moviesinfnet.model.LocalMovie
import com.infnet.moviesinfnet.repository.FirebaseRepository
import com.infnet.moviesinfnet.utils.status.BaseStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
@Suppress("EXPERIMENTAL_API_USAGE")
class SavedMoviesViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel()
{

    private val _toWatchMovies: MutableStateFlow<BaseStatus<List<LocalMovie>>> = MutableStateFlow(
        BaseStatus.Sleep
    )
    val toWatchMovies = _toWatchMovies.asStateFlow()

    private val _watchedMovies: MutableStateFlow<BaseStatus<List<LocalMovie>>> = MutableStateFlow(
        BaseStatus.Sleep
    )
    val watchedMovies = _watchedMovies.asStateFlow()

    init
    {
        viewModelScope.launch {
            firebaseRepository.getAllMovies().collectLatest { status ->
                Timber.d("All movies collected: $status")

                when (status)
                {
                    BaseStatus.Sleep ->
                    {
                        _toWatchMovies.value = status
                        _watchedMovies.value = status
                    }
                    BaseStatus.Loading ->
                    {
                        _toWatchMovies.value = status
                        _watchedMovies.value = status
                    }
                    is BaseStatus.Success ->
                    {
                        _toWatchMovies.value = BaseStatus.Success(status.data.filter {
                            it.isWatched == false
                        })
                        _watchedMovies.value = BaseStatus.Success(status.data.filter {
                            it.isWatched == true
                        })
                    }
                    is BaseStatus.Failed ->
                    {
                        _toWatchMovies.value = status
                        _watchedMovies.value = status
                    }
                }
            }
        }
    }

    override fun onCleared()
    {
        super.onCleared()
        Timber.d("SavedMoviesViewModel cleared")
        firebaseRepository.allMoviesListener?.removeListener()
    }
}