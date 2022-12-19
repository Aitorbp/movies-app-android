package com.example.movieapp.ui.main

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.data.database.Movie


@BindingAdapter("items")
fun RecyclerView.setItems(movies: List<Movie>?) {
    if (movies != null) {
        (adapter as? MoviesAdapter)?.submitList(movies)
    }
}