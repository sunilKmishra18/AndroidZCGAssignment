package com.sunilmishra.androidzcgassignment.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sunilmishra.androidzcgassignment.ui.composeui.CategoryListScreen
import com.sunilmishra.androidzcgassignment.ui.ui.theme.ZCGNavigationTheme
import com.sunilmishra.androidzcgassignment.utils.Routes
import com.sunilmishra.androidzcgassignment.viewmodels.MainViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            mainContent()
        }
    }

    @Composable
    fun mainContent(){
        val mainViewModel : MainViewModel = koinViewModel()
        ZCGNavigationTheme {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Routes.LIST_SCREEN) {
                composable(Routes.LIST_SCREEN) {
                    CategoryListScreen(navigation= navController, mainViewModel)
                }
            }
        }
    }
}