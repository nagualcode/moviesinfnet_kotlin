package com.infnet.moviesinfnet.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infnet.moviesinfnet.repository.FirebaseRepository
import com.infnet.moviesinfnet.utils.ext.trim
import com.infnet.moviesinfnet.utils.status.EventMessageStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel()
{
    val email: MutableLiveData<String> = MutableLiveData()
    val passwd: MutableLiveData<String> = MutableLiveData()
    val passwdConf: MutableLiveData<String> = MutableLiveData()

    private val _loginState = MutableStateFlow(LoginState.LOGIN)
    val loginState: StateFlow<LoginState> = _loginState

    var loginJob: Job? = null
    private val _state: MutableStateFlow<EventMessageStatus> = MutableStateFlow(EventMessageStatus.Sleep)
    val state = _state.asStateFlow()

    val user = repository.loggedUser

    fun changeState()
    {
        Timber.d("Change login state")
        _loginState.value =
                if (_loginState.value == LoginState.LOGIN) LoginState.REGISTRATION else LoginState.LOGIN
    }


    @ExperimentalCoroutinesApi
    fun signIn()
    {
        email.trim()
        passwd.trim()

        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            repository.loginUser(
                email = email.value,
                passwd = passwd.value
            ).collectLatest {
                _state.value = it
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun register()
    {
        email.trim()
        passwd.trim()
        passwdConf.trim()

        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            repository.registerUser(
                email = email.value,
                passwd = passwd.value,
                passwdConf = passwdConf.value,
            ).collectLatest {
                _state.value = it
            }
        }
    }

    enum class LoginState
    {
        LOGIN,
        REGISTRATION
    }
}

