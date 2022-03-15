package com.infnet.moviesinfnet.adapters.movieadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.RequestManager
import com.infnet.moviesinfnet.model.LocalMovie
import javax.inject.Inject


class MovieAdapter @Inject constructor(
    private val glide: RequestManager
) : ListAdapter<LocalMovie, MovieViewHolder>(MovieDiffCallback)
{
    lateinit var movieClickListener: MovieClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder =
            MovieViewHolder.create(parent)

    override fun onBindViewHolder(holderImage: MovieViewHolder, position: Int) =
            holderImage.bind(
                glide = glide,
                movieClickListener = movieClickListener,
                movie = getItem(position)
            )
}