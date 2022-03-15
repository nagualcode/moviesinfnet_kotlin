package com.infnet.moviesinfnet.adapters.movieadapter

import androidx.recyclerview.widget.DiffUtil
import com.infnet.moviesinfnet.model.LocalMovie


object MovieDiffCallback : DiffUtil.ItemCallback<LocalMovie>()
{
    override fun areItemsTheSame(oldItem: LocalMovie, newItem: LocalMovie): Boolean =
            oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: LocalMovie, newItem: LocalMovie): Boolean =
            oldItem == newItem
}