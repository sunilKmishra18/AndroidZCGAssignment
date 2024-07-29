package com.sunilmishra.androidzcgassignment.di

import com.sunilmishra.androidzcgassignment.data.Repository
import org.koin.dsl.module


val repositoryModule = module {
    factory {  Repository(get()) }
}