package com.sunilmishra.androidzcgassignment.viewmodel

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.sunilmishra.androidzcgassignment.di.networkModule
import com.sunilmishra.androidzcgassignment.di.remoteDataSourceModule
import com.sunilmishra.androidzcgassignment.di.repositoryModule
import com.sunilmishra.androidzcgassignment.di.viewModelModule
import com.sunilmishra.androidzcgassignment.viewmodels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q], manifest = Config.NONE)
class BaseViewModelTest {
    private lateinit var viewModel: BaseViewModel
    private lateinit var application: Application

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext()
        viewModel = object : BaseViewModel(application) {}
        // Initialize Koin only if it's not already started
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                // Koin module setup
                modules(listOf(networkModule, remoteDataSourceModule, repositoryModule, viewModelModule))
            }
        }
    }

    @Test
    fun `context property should return application context`() {
        // Arrange
        val expectedContext = application

        // Act
        val actualContext = viewModel.context

        // Assert
        assertEquals(expectedContext, actualContext)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

}