package com.infnet.moviesinfnet.ui.fragments

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.infnet.moviesinfnet.R
import com.infnet.moviesinfnet.model.LocalMovie
import com.infnet.moviesinfnet.utils.status.BaseStatus
import com.infnet.moviesinfnet.vm.SavedMoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow


@AndroidEntryPoint
class ToWatchFragment : RecyclerFragment()
{
    private val viewModel: SavedMoviesViewModel by activityViewModels()

    override val moviesToDisplay: Flow<BaseStatus<List<LocalMovie>>>
        get() = viewModel.toWatchMovies

    override val imgEmpty: Int
        get() = R.drawable.ic_outline_movie_24
    override val txtEmpty: Int
        get() = R.string.empty_result_towatch

    override fun actionToDetails(movie: LocalMovie)
    {
        findNavController().navigate(
            ToWatchFragmentDirections.actionToWatchFragmentToDetailsFragment(movieToDisplay = movie)
        )
    }
}