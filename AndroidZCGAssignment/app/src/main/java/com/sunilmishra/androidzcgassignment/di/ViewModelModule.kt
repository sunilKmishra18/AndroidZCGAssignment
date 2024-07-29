package com.sunilmishra.androidzcgassignment.di

import com.sunilmishra.androidzcgassignment.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule= module {
    viewModel{ MainViewModel(get(),get()) }
}


