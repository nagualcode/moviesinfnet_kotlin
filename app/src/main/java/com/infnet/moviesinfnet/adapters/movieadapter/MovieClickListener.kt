package com.infnet.moviesinfnet.adapters.movieadapter

import com.infnet.moviesinfnet.model.LocalMovie


data class MovieClickListener(
    val movieClickListener: (LocalMovie) -> Unit
)
{
    fun movieClick(localMovie: LocalMovie) = movieClickListener(localMovie)
}