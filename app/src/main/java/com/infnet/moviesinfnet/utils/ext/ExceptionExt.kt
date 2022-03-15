package com.infnet.moviesinfnet.utils.ext

import com.infnet.moviesinfnet.R
import com.infnet.moviesinfnet.utils.helper.Event
import com.infnet.moviesinfnet.utils.helper.Message


val Exception.requireMessage: String
    get() = localizedMessage ?: message ?: ""

val Exception.makeEvent: Event<Message>
    get() = Event(Message(R.string.something_went_wrong, listOf(requireMessage)))