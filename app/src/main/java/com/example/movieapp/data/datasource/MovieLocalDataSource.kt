package com.example.movieapp.data.datasource

import com.example.movieapp.data.database.Movie
import com.example.movieapp.data.database.MovieDao
import kotlinx.coroutines.flow.Flow

class MovieLocalDataSource(private val movieDao: MovieDao) {

    val movies: Flow<List<Movie>> = movieDao.getAll()

    suspend fun isEmpty(): Boolean = movieDao.movieCount() == 0

    fun findById(id: Int): Flow<Movie> = movieDao.findById(id)

    fun findByMovie(title: String): Flow<List<Movie>> = movieDao.findByMovie(title)

    suspend fun save(movies: List<Movie>) {
        movieDao.insertMovies(movies)
    }

    suspend fun size(): Int = movieDao.movieCount()
}