package com.sunilmishra.androidzcgassignment.di

import android.os.Build
import com.sunilmishra.androidzcgassignment.utils.Constants
import com.sunilmishra.androidzcgassignment.utils.Constants.Companion.BASE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q], manifest = Config.NONE)
class NetworkModuleTest {

    private val maxRetryCount = 3
    private lateinit var retryInterceptor: RetryInterceptor
    private lateinit var chain: Interceptor.Chain

    @Before
    fun setup() {
        retryInterceptor = RetryInterceptor(maxRetryCount)
        chain = mock(Interceptor.Chain::class.java)
        // Initialize Koin only if it's not already started
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                // Koin module setup
                modules(listOf(networkModule, remoteDataSourceModule, repositoryModule, viewModelModule))
            }
        }
    }

    @Test
    fun `test retry interceptor retries on failure`() {
        val request = Request.Builder().url(Constants.BASE_URL).build()
        `when`(chain.request()).thenReturn(request)
        val mockResponse = mock(Response::class.java)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(chain.proceed(request)).thenThrow(IOException::class.java)

        try {
            retryInterceptor.intercept(chain)
        } catch (e: IOException) {
            // Ensure that retries occurred as expected
            assertEquals(maxRetryCount, retryInterceptor.getRetryCount()-1)
        }
    }

    @Test
    fun `test provideHttpClient configures OkHttpClient`() {
        val client = provideHttpClient()

        // Verify that the client has the retry interceptor
        val interceptors = client.interceptors
        assertTrue(interceptors.any { it is RetryInterceptor })

        // Verify logging interceptor
        assertTrue(interceptors.any { it is HttpLoggingInterceptor })
    }

    @Test
    fun `test provideRetrofit configures Retrofit`() {
        val okHttpClient = provideHttpClient()
        val gsonConverterFactory = provideConverterFactory()

        val retrofit = provideRetrofit(okHttpClient, gsonConverterFactory)

        // Check base URL
        assertEquals(BASE_URL, retrofit.baseUrl().toString())

        // Check converter factory
        assertTrue(retrofit.converterFactories().contains(gsonConverterFactory))

        // Check OkHttpClient
        assertEquals(okHttpClient, retrofit.callFactory() as OkHttpClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }
}