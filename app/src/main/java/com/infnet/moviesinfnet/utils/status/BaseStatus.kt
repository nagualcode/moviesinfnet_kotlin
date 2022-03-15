package com.infnet.moviesinfnet.utils.status

import com.infnet.moviesinfnet.utils.helper.Event
import com.infnet.moviesinfnet.utils.helper.Message


sealed class BaseStatus<out T>
{
    object Sleep : BaseStatus<Nothing>()
    object Loading : BaseStatus<Nothing>()
    data class Success<T>(val data: T) : BaseStatus<T>()
    data class Failed(val errorEvent: Event<Message>) : BaseStatus<Nothing>()
}
