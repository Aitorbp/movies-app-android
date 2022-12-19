package com.example.movieapp.data


import com.example.movieapp.R
import com.example.movieapp.App
import com.example.movieapp.data.database.Movie
import com.example.movieapp.data.datasource.MovieLocalDataSource
import com.example.movieapp.data.datasource.MovieRemoteDataSource
import kotlinx.coroutines.flow.Flow

class MoviesRepository(application: App) {

    private val regionRepository = RegionRepository(application)
    private val localDataSource = MovieLocalDataSource(application.db.movieDao())
    private val remoteDataSource = MovieRemoteDataSource(application.getString(R.string.api_key))

    companion object {
        const val PAG_SIZE = 10
        const val PAG_THRESHOLD = 6
        const val INITIAL_PAGE = 1
    }

    val popularMovies = localDataSource.movies

    fun findById(id: Int): Flow<Movie> = localDataSource.findById(id)

    fun findByMovie(title: String): Flow<List<Movie>> = localDataSource.findByMovie(title)

    suspend fun requestPopularMovies(): Error? = tryCall {
        if (localDataSource.isEmpty()) {
            val movies = remoteDataSource.findPopularMovies(INITIAL_PAGE, regionRepository.findLastRegion())
            localDataSource.save(movies.results.toLocalModel())
        }
    }

    suspend fun requestPopularMoviesAfterFilter(): Error? = tryCall {

            val movies = remoteDataSource.findPopularMovies(INITIAL_PAGE, regionRepository.findLastRegion())
            localDataSource.save(movies.results.toLocalModel())

    }

    suspend fun switchFavorite(movie: Movie): Error? = tryCall {
        val updatedMovie = movie.copy(favorite = !movie.favorite)
        localDataSource.save(listOf(updatedMovie))
    }

    suspend fun checkRequireNewPage(lastVisibleItem: Int) {
        val size = localDataSource.size()

        if(lastVisibleItem >= size - PAG_THRESHOLD) {
            val page = size / PAG_SIZE + 1
            val newMovies = remoteDataSource.findPopularMovies(page, regionRepository.findLastRegion())
            localDataSource.save(newMovies.results.toLocalModel())
        }
    }

}

private fun List<RemoteMovie>.toLocalModel(): List<Movie> = map { it.toLocalModel() }

private fun RemoteMovie.toLocalModel(): Movie = Movie(
    id,
    title,
    overview,
    releaseDate,
    posterPath,
    backdropPath ?: "",
    originalLanguage,
    originalTitle,
    popularity,
    voteAverage,
    false
)