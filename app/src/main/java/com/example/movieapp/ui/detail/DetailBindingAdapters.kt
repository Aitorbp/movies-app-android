package com.example.movieapp.ui.detail

import androidx.databinding.BindingAdapter
import com.example.movieapp.data.database.Movie


@BindingAdapter("movie")
fun MovieDetailInfoView.updateMovieDetails(movie: Movie?) {
    if (movie != null) {
        setMovie(movie)
    }
}