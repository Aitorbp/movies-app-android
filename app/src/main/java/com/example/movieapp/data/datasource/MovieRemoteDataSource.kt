package com.example.movieapp.data.datasource

import com.example.movieapp.data.RemoteConnection

class MovieRemoteDataSource(private val apiKey: String) {

    suspend fun findPopularMovies(page: Int, region: String) =
        RemoteConnection.service.listPopularMovies(apiKey, region, page)
}