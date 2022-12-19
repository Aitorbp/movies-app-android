package com.example.movieapp.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.Error
import com.example.movieapp.data.MoviesRepository
import com.example.movieapp.data.database.Movie
import com.example.movieapp.data.toError
import com.example.movieapp.ui.detail.DetailViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val moviesRepository: MoviesRepository) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()


    init {
        notifyLastVisible(0)
        viewModelScope.launch {
            moviesRepository.popularMovies
                .catch { cause -> _state.update { it.copy(error = cause.toError()) } }
                .collect { movies -> _state.update { UiState(movies = movies) } }
        }
    }

    fun onUiReady() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            val error = moviesRepository.requestPopularMovies()
            _state.update { _state.value.copy(loading = false, error = error) }
        }
    }

    fun notifyLastVisible(lastVisibleItem: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            moviesRepository.checkRequireNewPage(lastVisibleItem)
            _state.value = _state.value.copy(loading = false)
        }

    }

    fun filterMovie(filter: String?) {
        viewModelScope.launch {
            if (filter.toString() == "") {
                Log.d("aitor", "jasdjkaksdk")
                _state.value = _state.value.copy(loading = true)
                val error = moviesRepository.requestPopularMoviesAfterFilter()
                _state.update { _state.value.copy(loading = false, error = error) }
            }
        }
        viewModelScope.launch {

            moviesRepository.findByMovie(filter.toString())
                .catch { cause -> _state.update { it.copy(error =  cause.toError()) } }
                .collect {   movie -> _state.update {  UiState(movies = movie) } }

        }
    }


    data class UiState(
        val loading: Boolean = false,
        val movies: List<Movie>? = null,
        val error: Error? = null
    )
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val moviesRepository: MoviesRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(moviesRepository) as T
    }
}