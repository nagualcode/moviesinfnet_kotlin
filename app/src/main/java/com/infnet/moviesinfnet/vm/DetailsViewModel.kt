package com.infnet.moviesinfnet.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infnet.moviesinfnet.model.LocalMovie
import com.infnet.moviesinfnet.repository.FirebaseRepository
import com.infnet.moviesinfnet.utils.status.EventMessageStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject



@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel()
{
    private val _addingState: MutableStateFlow<EventMessageStatus> = MutableStateFlow(
        EventMessageStatus.Sleep
    )
    val addingState = _addingState.asStateFlow()

    @ExperimentalCoroutinesApi
    fun addMovieToWatch(movie: LocalMovie, isWatched: Boolean)
    {

        if (_addingState.value != EventMessageStatus.Loading)
        {
            viewModelScope.launch {
                firebaseRepository.addMovie(movie, isWatched).collectLatest {
                    _addingState.value = it
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getMovieIsWatched(movieId: String) = firebaseRepository.getMovieIsWatched(movieId)

    override fun onCleared()
    {
        super.onCleared()
        Timber.d("DetailsViewModel cleared")
        firebaseRepository.isWatchedListener?.removeListener()
    }

    fun removeMovie(movieId: String) = firebaseRepository.removeMovie(movieId)

}