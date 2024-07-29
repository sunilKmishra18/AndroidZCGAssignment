package com.sunilmishra.androidzcgassignment.ui.composeui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.sunilmishra.androidzcgassignment.data.model.Item
import com.sunilmishra.androidzcgassignment.data.model.Section
import com.sunilmishra.androidzcgassignment.utils.UiState
import com.sunilmishra.androidzcgassignment.utils.Utils
import com.sunilmishra.androidzcgassignment.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CategoryListScreen(navigation: NavController, mainViewModel: MainViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val state by mainViewModel.uiStateCategoriesList.collectAsState()
    val context = LocalContext.current
    val pullRefreshState = rememberPullRefreshState(
        isRefreshing,
        {
            coroutineScope.launch {
                isRefreshing = true
                mainViewModel.getCategoriesList(navigation.context)
                isRefreshing = false
            }
        }
    )

    Scaffold(
        topBar = {
            CustomToolbarScreen(navController = navigation, title = "Categories", false)
        }
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp)
            ) {
                // Fetch the categories list on composition
                item {
                    LaunchedEffect(Unit) {
                        if (!Utils.hasInternetConnection(context)) {
                            Toast.makeText(
                                navigation.context,
                                "No internet connection",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            getCategoriesListAPI(context, mainViewModel)
                        }
                    }
                }

                // Observe the state from the ViewModel
                when (state) {
                    is UiState.Success -> {
                        items((state as UiState.Success<List<Section>>).data ?: emptyList()) { section ->
                            SectionList(section)
                        }
                    }
                    is UiState.Loading -> {
                        item {
                            ProgressLoader(isLoading = true)
                        }
                    }
                    is UiState.Error -> {
                        item {
                            ProgressLoader(isLoading = false)
                            // Handle Error
                            Text(
                                text = (state as UiState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun SectionList(section: Section) {
    when (section.sectionType) {
        "banner" -> BannerSection(section.sectionType, section.items)
        "horizontalFreeScroll" -> HorizontalScrollSection(section.sectionType, section.items)
        "splitBanner" -> SplitBannerSection(section.sectionType, section.items)
    }
}

@Composable
fun BannerSection(sectionType: String, items: List<Item>) {
    val capitalizedSectionType =
        sectionType.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    Column {
        Text(text = capitalizedSectionType, style = MaterialTheme.typography.headlineLarge)
        items.forEach { item ->
            Image(
                painter = rememberAsyncImagePainter(item.image),
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun HorizontalScrollSection(sectionType: String, items: List<Item>) {
    val capitalizedSectionType =
        sectionType.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    Column {
        Text(
            text = capitalizedSectionType,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(8.dp)
        )
        LazyRow {
            items(items.size) { index ->
                Column(modifier = Modifier.padding(8.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(items[index].image),
                        contentDescription = items[index].title,
                        modifier = Modifier.size(150.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(text = items[index].title, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun SplitBannerSection(sectionType: String, items: List<Item>) {
    val capitalizedSectionType =
        sectionType.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    Column {
        Text(
            text = capitalizedSectionType,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            items.forEach { item ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(item.image),
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

private fun getCategoriesListAPI(context: Context, mainViewModel: MainViewModel) {
    // Call the function to fetch categories list
    mainViewModel.getCategoriesList(context)
}