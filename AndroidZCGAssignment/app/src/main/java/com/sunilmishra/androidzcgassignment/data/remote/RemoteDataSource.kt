package com.sunilmishra.androidzcgassignment.data.remote

class RemoteDataSource(private val apiService: ApiService) {
    suspend fun getCategories() = apiService.getCategories()
}