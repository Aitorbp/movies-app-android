package com.example.movieapp.ui.detail


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.movieapp.R
import com.example.movieapp.data.MoviesRepository
import com.example.movieapp.databinding.FragmentDetailBinding
import com.example.movieapp.ui.common.app
import com.example.movieapp.ui.common.launchAndCollect


class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val safeArgs: DetailFragmentArgs by navArgs()

    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(safeArgs.movieId, MoviesRepository(requireActivity().app))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDetailBinding.bind(view)

        binding.movieDetailToolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.movieDetailFavorite.setOnClickListener { viewModel.onFavoriteClicked() }

        viewLifecycleOwner.launchAndCollect(viewModel.state) { state ->
            if (state.movie != null) {
                binding.movie = state.movie
            }
        }
    }
}