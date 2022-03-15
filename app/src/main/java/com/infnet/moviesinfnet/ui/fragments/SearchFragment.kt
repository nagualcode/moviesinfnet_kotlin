package com.infnet.moviesinfnet.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.infnet.moviesinfnet.R
import com.infnet.moviesinfnet.model.LocalMovie
import com.infnet.moviesinfnet.utils.ext.hideKeyboard
import com.infnet.moviesinfnet.utils.status.BaseStatus
import com.infnet.moviesinfnet.vm.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class SearchFragment : RecyclerFragment()
{
    private val viewModel: SearchViewModel by activityViewModels()

    override val moviesToDisplay: Flow<BaseStatus<List<LocalMovie>>>
        get() = viewModel.moviesToDisplay

    override fun actionToDetails(movie: LocalMovie)
    {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToDetailsFragment(
                movieToDisplay = movie,
            )
        )
    }

    override val imgEmpty: Int
        get() = R.drawable.ic_outline_search_off_24
    override val txtEmpty: Int
        get() = R.string.empty_result_search


    private var menuItemSearch: MenuItem? = null
    private var searchView: SearchView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar_search, menu)

        // region SearchView setup

        menuItemSearch = menu.findItem(R.id.itemSearch)
        searchView = menuItemSearch!!.actionView as SearchView

        // change searchText if it has been set
        viewModel.query.value?.let {
            menuItemSearch!!.expandActionView()
            searchView!!.setQuery(it, false)
            searchView!!.clearFocus()
        }

        searchView!!.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener
            {
                override fun onQueryTextSubmit(textInput: String?): Boolean
                {
                    hideKeyboard()
                    viewModel.search(textInput)
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean = true
            }
        )

        menuItemSearch!!.setOnActionExpandListener(
            object : MenuItem.OnActionExpandListener
            {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean = true

                /**
                 * collapsing search menu, show trending news again
                 */
                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean
                {
                    viewModel.search(null)
                    return true
                }
            }
        )

        // endregion
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.miLogOut ->
            {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.sign_out))
                    .setMessage(resources.getString(R.string.log_out_confirmation))
                    .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
                    }
                    .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                        viewModel.logOut()
                    }
                    .show()

                true
            }
            else ->
            {
                super.onOptionsItemSelected(item)
            }
        }
    }

}