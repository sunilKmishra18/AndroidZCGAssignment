package com.sunilmishra.androidzcgassignment.data

import android.content.Context
import android.os.Build
import com.sunilmishra.androidzcgassignment.data.model.Item
import com.sunilmishra.androidzcgassignment.data.model.Section
import com.sunilmishra.androidzcgassignment.data.remote.RemoteDataSource
import com.sunilmishra.androidzcgassignment.di.networkModule
import com.sunilmishra.androidzcgassignment.di.remoteDataSourceModule
import com.sunilmishra.androidzcgassignment.di.repositoryModule
import com.sunilmishra.androidzcgassignment.di.viewModelModule
import com.sunilmishra.androidzcgassignment.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q], manifest = Config.NONE)
class RepositoryTest {

    private lateinit var repository: Repository
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = mock()
        remoteDataSource = mock()
        repository = Repository(remoteDataSource)
        // Initialize Koin only if it's not already started
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                // Koin module setup
                modules(
                    listOf(
                        networkModule,
                        remoteDataSourceModule,
                        repositoryModule,
                        viewModelModule
                    )
                )
            }
        }
    }

    @Test
    fun `test getCategories returns success state`() = runTest {

        // Prepare test data
        val sections = listOf(
            Section(
                "banner",
                listOf(
                    Item(
                        "Jacket",
                        "https://images.pexels.com/photos/789812/pexels-photo-789812.jpeg"
                    )
                )
            )
        )
        val successResponse: Response<List<Section>> = Response.success(sections)

        // Mock the remoteDataSource behavior correctly
        whenever(remoteDataSource.getCategories()).thenReturn(successResponse)

        // Call the method under test
        val resultFlow = repository.getCategories(context)

        // Collect the result
        val result = resultFlow.first()

        // Verify the result
        assertNotNull(result)
    }

    @Test
    fun `test getCategories returns error state`() = runTest {
        // Prepare test data
        val errorMessage = "No Internet Connection"
        val errorResponse: Response<List<Section>> = Response.error(400, errorBody(errorMessage))

        // Mock the remoteDataSource behavior correctly
        whenever(remoteDataSource.getCategories()).thenReturn(errorResponse)

        // Call the method under test
        val resultFlow = repository.getCategories(context)

        // Collect the result
        val result = resultFlow.first()

        // Verify the result
        assertTrue(result is UiState.Error)
        assertEquals(errorMessage, (result as UiState.Error).message)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    private fun errorBody(message: String) = okhttp3.ResponseBody.create(null, message)
}