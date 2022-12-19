package com.example.movieapp.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R

import com.example.movieapp.data.MoviesRepository
import com.example.movieapp.databinding.FragmentMainBinding
import com.example.movieapp.ui.common.app
import com.example.movieapp.ui.common.launchAndCollect
import kotlinx.coroutines.flow.filter

class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(MoviesRepository(requireActivity().app))
    }

    private lateinit var mainState: MainState

    private val adapter = MoviesAdapter { mainState.onMovieClicked(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainState = buildMainState()

        val binding = FragmentMainBinding.bind(view).apply {
            recycler.adapter = adapter

            val layoutManager = recycler.layoutManager as GridLayoutManager

            recycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    viewModel.notifyLastVisible(layoutManager.findLastVisibleItemPosition())
                }
            })
        }

        viewLifecycleOwner.launchAndCollect(viewModel.state) {
            binding.loading = it.loading
            binding.movies = it.movies
            binding.error = it.error?.let(mainState::errorToString)
        }

        binding.etFilter.addTextChangedListener { filter ->
            Log.i("aitor", filter.toString())
                    viewModel.filterMovie(filter.toString())
        }

        mainState.requestLocationPermission {
            viewModel.onUiReady()
        }
    }
}