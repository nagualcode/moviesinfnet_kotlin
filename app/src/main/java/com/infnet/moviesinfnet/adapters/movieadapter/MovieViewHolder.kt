package com.infnet.moviesinfnet.adapters.movieadapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.infnet.moviesinfnet.databinding.MovieItemBinding
import com.infnet.moviesinfnet.model.LocalMovie
import com.infnet.moviesinfnet.utils.ext.dpToPx

class MovieViewHolder private constructor(
    private val binding: MovieItemBinding
) : RecyclerView.ViewHolder(binding.root)

{
    companion object
    {
        fun create(parent: ViewGroup): MovieViewHolder
        {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = MovieItemBinding.inflate(layoutInflater, parent, false)
            return MovieViewHolder(
                binding
            )
        }
    }


    fun bind(
        glide: RequestManager,
        movieClickListener: MovieClickListener,
        movie: LocalMovie
    )
    {

        with(binding)
        {

            proBarImageLoading.isVisible = true

            glide
                .load(movie.fullPath)
                .listener(
                    object : RequestListener<Drawable>
                    {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean
                        {
                            proBarImageLoading.isVisible = false

                            with(imgPoster)
                            {
                                scaleType = ImageView.ScaleType.FIT_CENTER
                                setPadding(16.dpToPx)
                            }

                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean
                        {
                            proBarImageLoading.isVisible = false

                            with(imgPoster)
                            {
                                scaleType = ImageView.ScaleType.CENTER_CROP
                                setPadding(0)
                            }

                            return false
                        }

                    })
                .into(imgPoster)
        }

        binding.movie = movie
        binding.movieClickListener = movieClickListener
        binding.executePendingBindings()
    }
}