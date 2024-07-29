package com.sunilmishra.androidzcgassignment.di

import android.util.Log
import com.sunilmishra.androidzcgassignment.data.remote.ApiService
import com.sunilmishra.androidzcgassignment.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier


// Create a logging interceptor
val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

// Create a custom hostname verifier
val hostnameVerifier = HostnameVerifier { hostname, session ->
    // Implement your verification logic here
    hostname == "www.jsonkeeper.com" || hostname == "jsonkeeper.com"
}

// Define a retry interceptor
class RetryInterceptor(private val maxRetryCount: Int) : okhttp3.Interceptor {
    private var retryCount = 0

    @Throws(IOException::class)
    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        retryCount = 0
        var response: okhttp3.Response
        while (true) {
            try {
                response = chain.proceed(chain.request())
                if (response.isSuccessful) {
                    return response
                }
            } catch (e: IOException) {
                if (++retryCount > maxRetryCount) {
                    throw e
                }
                // Optional: Add delay between retries
                Thread.sleep(1000)
            }
        }
    }

    fun getRetryCount(): Int = retryCount
}

fun provideHttpClient(): OkHttpClient {
    return OkHttpClient
        .Builder()
        .addInterceptor(logging)
        .addInterceptor(RetryInterceptor(maxRetryCount = 3)) // Adjust the retry count as needed
        .hostnameVerifier(hostnameVerifier)
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()
}


fun provideConverterFactory(): GsonConverterFactory =
    GsonConverterFactory.create()


fun provideRetrofit(
    okHttpClient: OkHttpClient,
    gsonConverterFactory: GsonConverterFactory
): Retrofit {
    Log.i("NetworkModule","Base URL:${BASE_URL}")
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(gsonConverterFactory)
        .build()
}

fun provideService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)


val networkModule= module {
    single { provideHttpClient() }
    single { provideConverterFactory() }
    single { provideRetrofit(get(),get()) }
    single { provideService(get()) }
}
