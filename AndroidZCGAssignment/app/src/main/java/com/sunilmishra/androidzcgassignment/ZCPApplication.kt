package com.sunilmishra.androidzcgassignment

import android.app.Application
import com.sunilmishra.androidzcgassignment.di.networkModule
import com.sunilmishra.androidzcgassignment.di.remoteDataSourceModule
import com.sunilmishra.androidzcgassignment.di.repositoryModule
import com.sunilmishra.androidzcgassignment.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin



class ZCPApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ZCPApplication)
            androidLogger()
            modules(networkModule, remoteDataSourceModule, repositoryModule, viewModelModule)
        }
    }
}