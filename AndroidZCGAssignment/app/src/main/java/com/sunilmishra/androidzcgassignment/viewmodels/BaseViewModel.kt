package com.sunilmishra.androidzcgassignment.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel


open class BaseViewModel(application: Application) : AndroidViewModel(application) {
  val context
    get() = getApplication<Application>()
}