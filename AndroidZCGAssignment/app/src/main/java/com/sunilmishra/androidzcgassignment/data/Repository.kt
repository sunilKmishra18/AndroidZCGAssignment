package com.sunilmishra.androidzcgassignment.data

import android.content.Context
import com.sunilmishra.androidzcgassignment.data.model.Section
import com.sunilmishra.androidzcgassignment.data.remote.RemoteDataSource
import com.sunilmishra.androidzcgassignment.data.remote.toResultFlow
import com.sunilmishra.androidzcgassignment.utils.UiState
import kotlinx.coroutines.flow.Flow

class Repository(private val remoteDataSource: RemoteDataSource) {

    suspend fun getCategories(context: Context): Flow<UiState<List<Section>>> {
        return toResultFlow(context){
            remoteDataSource.getCategories()
        }
    }
}