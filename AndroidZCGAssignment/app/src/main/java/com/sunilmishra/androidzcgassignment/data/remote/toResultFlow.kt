package com.sunilmishra.androidzcgassignment.data.remote


import android.content.Context
import com.sunilmishra.androidzcgassignment.utils.Constants
import com.sunilmishra.androidzcgassignment.utils.UiState
import com.sunilmishra.androidzcgassignment.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

inline fun <reified T> toResultFlow(context: Context, crossinline call: suspend () -> Response<T>?): Flow<UiState<T>> {
    return flow {
        val isInternetConnected = Utils.hasInternetConnection(context)
        if (isInternetConnected) {
            emit(UiState.Loading)
            try {
                val c = call()
                if (c?.isSuccessful == true && c.body() != null) {
                    emit(UiState.Success(c.body()))
                } else {
                    if (c != null) {
                        emit(UiState.Error(c.message()))
                    }
                }
            } catch (e: Exception) {
                emit(UiState.Error(e.toString()))
            }
        } else {
            emit(UiState.Error(Constants.API_INTERNET_MESSAGE))
        }
    }.flowOn(Dispatchers.IO)
}

