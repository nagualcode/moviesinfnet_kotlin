package com.infnet.moviesinfnet.utils.status

import com.infnet.moviesinfnet.utils.helper.Event
import com.infnet.moviesinfnet.utils.helper.Message


sealed class EventMessageStatus
{
    object Sleep : EventMessageStatus()
    object Loading : EventMessageStatus()
    data class Success(val successEvent: Event<Message>) : EventMessageStatus()
    data class Failed(val errorEvent: Event<Message>) : EventMessageStatus()
}
