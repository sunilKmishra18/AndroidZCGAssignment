package com.sunilmishra.androidzcgassignment.data.remote

import com.sunilmishra.androidzcgassignment.data.model.Section
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("b/5BEJ")
    suspend fun getCategories(): Response<List<Section>>
}