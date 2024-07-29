package com.sunilmishra.androidzcgassignment.di

import com.sunilmishra.androidzcgassignment.data.remote.RemoteDataSource
import org.koin.dsl.module

val remoteDataSourceModule= module {
    factory {  RemoteDataSource(get()) }
}
