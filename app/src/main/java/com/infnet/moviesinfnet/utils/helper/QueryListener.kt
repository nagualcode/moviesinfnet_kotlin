package com.infnet.moviesinfnet.utils.helper

import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener


data class QueryListener(
    val query: Query,
    val valueEventListener: ValueEventListener
)
{
    fun removeListener()
    {
        query.removeEventListener(valueEventListener)
    }

    fun addListener()
    {
        query.addValueEventListener(valueEventListener)
    }
}
