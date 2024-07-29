package com.sunilmishra.androidzcgassignment.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sunilmishra.androidzcgassignment.data.Repository
import com.sunilmishra.androidzcgassignment.data.model.Section
import com.sunilmishra.androidzcgassignment.di.networkModule
import com.sunilmishra.androidzcgassignment.di.remoteDataSourceModule
import com.sunilmishra.androidzcgassignment.di.repositoryModule
import com.sunilmishra.androidzcgassignment.di.viewModelModule
import com.sunilmishra.androidzcgassignment.utils.UiState
import com.sunilmishra.androidzcgassignment.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q], manifest = Config.NONE)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var repository: Repository

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        // Initialize Koin only if it's not already started
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                // Koin module setup
                modules(listOf(networkModule, remoteDataSourceModule, repositoryModule, viewModelModule))
            }
        }
        val application = mock(Application::class.java)
        viewModel = MainViewModel(repository, application)
    }

    @Test
    fun `getCategoriesList should emit success state`() = runTest {
        // Given
        val sections = listOf(Section("banner", listOf()))
        val flow = flow { emit(UiState.Success(sections)) }
        `when`(repository.getCategories(context)).thenReturn(flow)

        // When
        viewModel.getCategoriesList(context)

        // Allow some time for the flow to emit
        advanceUntilIdle()

        // Then
        val state = viewModel.uiStateCategoriesList.value
        assert(state is UiState.Success)
        assert((state as UiState.Success).data == sections)
    }

    @Test
    fun `getCategoriesList should emit loading state`() = runTest {
        // Given
        val flow = flow { emit(UiState.Loading) }
        `when`(repository.getCategories(context)).thenReturn(flow)

        // When
        viewModel.getCategoriesList(context)

        // Allow some time for the flow to emit
        advanceUntilIdle()

        // Then
        val state = viewModel.uiStateCategoriesList.value
        Assert.assertTrue(state is UiState.Loading)
    }

    @Test
    fun `getCategoriesList should emit error state`() = runTest {
        // Given
        val errorMessage = "An error occurred"
        // Create a flow that emits an error state
        val flow = flow{
            emit(UiState.Error(errorMessage))
        }
        `when`(repository.getCategories(context)).thenReturn(flow)

        // When
        viewModel.getCategoriesList(context)

        // Allow some time for the flow to emit
        advanceUntilIdle()

        // Then
        val state = viewModel.uiStateCategoriesList.value
        Assert.assertTrue(state is UiState.Error)
        Assert.assertEquals(errorMessage, (state as UiState.Error).message)
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}