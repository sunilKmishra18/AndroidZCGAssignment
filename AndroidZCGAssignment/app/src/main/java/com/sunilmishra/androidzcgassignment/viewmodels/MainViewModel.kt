package com.sunilmishra.androidzcgassignment.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sunilmishra.androidzcgassignment.data.Repository
import com.sunilmishra.androidzcgassignment.data.model.Section
import com.sunilmishra.androidzcgassignment.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository, application: Application): BaseViewModel(application) {


    private val _uiStateCategoriesList = MutableStateFlow<UiState<List<Section>>>(UiState.Loading)
    val uiStateCategoriesList: StateFlow<UiState<List<Section>>> = _uiStateCategoriesList

    fun getCategoriesList(context: Context) = viewModelScope.launch {
        repository.getCategories(context).collect {
            when (it) {
                is UiState.Success -> {
                    Log.i("MainViewModel","Success...")
                    _uiStateCategoriesList.value = UiState.Success(it.data)
                }
                is UiState.Loading -> {
                    Log.i("MainViewModel","Loading...")
                    _uiStateCategoriesList.value = UiState.Loading
                }
                is UiState.Error -> {
                    //Handle Error
                    Log.i("MainViewModel", "Error: ${it.message}")
                    _uiStateCategoriesList.value = UiState.Error(it.message)
                }
            }
        }
    }

}