package com.example.discogsviewer.navigation

import android.content.Intent
import android.os.Build
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.discogsviewer.R
import com.example.discogsviewer.ScreenRoute
import com.example.discogsviewer.details.feature.ReleaseDetailsViewModel
import com.example.discogsviewer.details.feature.ui.ReleaseDetailsScreen
import com.example.discogsviewer.favorites.feature.FavoritesViewModel
import com.example.discogsviewer.favorites.feature.ui.FavoriteSortBottomSheet
import com.example.discogsviewer.favorites.feature.ui.FavoritesScreen
import com.example.discogsviewer.releases.feature.ReleasesViewModel
import com.example.discogsviewer.releases.feature.ui.ReleasesScreen
import com.example.discogsviewer.search.feature.ReleaseSearchViewModel
import com.example.discogsviewer.search.feature.ui.SearchScreen
import com.example.discogsviewer.settings.SettingsViewModel
import com.example.discogsviewer.settings.ui.SettingsScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val screens = listOf(
        ScreenRoute.TopReleases,
        ScreenRoute.Search,
        ScreenRoute.Favorites,
    )
    val bottomNavRoutes = screens.map { it.route }
    val scrollCallbacks = remember { mutableStateMapOf<String, () -> Unit>() }
    val savedListStates = remember { mutableStateMapOf<String, LazyListState>() }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = stringResource(screen.titleRes),
                            )
                        },
                        label = { Text(stringResource(screen.titleRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            val currentRoute = currentDestination?.route ?: return@NavigationBarItem
                            val previousRoute =
                                navController.previousBackStackEntry?.destination?.route

                            if (currentRoute == screen.route) {
                                scrollCallbacks[currentRoute]?.invoke()
                                return@NavigationBarItem
                            }

                            val isCurrentRouteBottomNav = bottomNavRoutes.contains(currentRoute)

                            if (!isCurrentRouteBottomNav) {
                                if (previousRoute == screen.route) {
                                    navController.popBackStack()
                                } else {
                                    navController.navigate(screen.route) {
                                        popUpTo(currentRoute) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                return@NavigationBarItem
                            }

                            navController.navigate(screen.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreenRoute.TopReleases.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = ScreenRoute.TopReleases.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) },
            ) {
                val listState = savedListStates.getOrPut(ScreenRoute.TopReleases.route) {
                    rememberLazyListState()
                }

                ReleasesListScreenContent(
                    listState = listState,
                    navController = navController,
                    onScrollToTopProvider = { callback ->
                        scrollCallbacks[ScreenRoute.TopReleases.route] = callback
                    }
                )
            }
            composable(
                route = ScreenRoute.Details.route,
                enterTransition = {
                    slideInHorizontally(animationSpec = tween(300)) { it } + fadeIn(
                        animationSpec = tween(
                            300
                        )
                    )
                },
                exitTransition = {
                    slideOutHorizontally(animationSpec = tween(300)) { -it }
                },
                popEnterTransition = {
                    slideInHorizontally(animationSpec = tween(300)) { -it }
                },
                popExitTransition = {
                    slideOutHorizontally(animationSpec = tween(300)) { it } + fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                },
            ) {
                DetailsScreenContent(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = ScreenRoute.Search.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) },
            ) {
                val listState = savedListStates.getOrPut(ScreenRoute.Search.route) {
                    rememberLazyListState()
                }
                SearchScreenContent(
                    listState = listState,
                    navController = navController,
                    onScrollToTopProvider = { callback ->
                        scrollCallbacks[ScreenRoute.Search.route] = callback
                    }
                )
            }

            composable(
                route = ScreenRoute.Favorites.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) },
            ) {
                val listState = savedListStates.getOrPut(ScreenRoute.Favorites.route) {
                    rememberLazyListState()
                }

                FavoritesScreenContent(
                    listState = listState,
                    navController = navController,
                    onScrollToTopProvider = { callback ->
                        scrollCallbacks[ScreenRoute.Favorites.route] = callback
                    }
                )
            }

            composable(
                route = ScreenRoute.Settings.route,
                enterTransition = {
                    slideInHorizontally(animationSpec = tween(300)) { it } + fadeIn(
                        animationSpec = tween(
                            300
                        )
                    )
                },
                exitTransition = {
                    slideOutHorizontally(animationSpec = tween(300)) { -it }
                },
                popEnterTransition = {
                    slideInHorizontally(animationSpec = tween(300)) { -it }
                },
                popExitTransition = {
                    slideOutHorizontally(animationSpec = tween(300)) { it } + fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                },
            ) {
                SettingsScreenContent(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun ReleasesListScreenContent(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    navController: NavController,
    onScrollToTopProvider: ((() -> Unit)) -> Unit = {},
) {
    val viewModel: ReleasesViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val scrollToTop: () -> Unit = { listState.requestScrollToItem(0) }
    val currentScrollToTop by rememberUpdatedState(scrollToTop)
    SideEffect {
        onScrollToTopProvider(currentScrollToTop)
    }

    ReleasesScreen(
        modifier = modifier,
        state = state,
        listState = listState,
        onRefresh = viewModel::refresh,
        onItemClicked = { releaseId ->
            navController.navigate("details_screen/$releaseId")
        },
        onErrorShown = viewModel::errorHasShown,
        onToggleFavorite = viewModel::onToggleFavorite,
    )
}

@Composable
fun SearchScreenContent(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    navController: NavController,
    onScrollToTopProvider: ((() -> Unit)) -> Unit = {},
) {
    val viewModel: ReleaseSearchViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val scrollToTop: () -> Unit = { listState.requestScrollToItem(0) }
    val currentScrollToTop by rememberUpdatedState(scrollToTop)
    SideEffect {
        onScrollToTopProvider(currentScrollToTop)
    }

    SearchScreen(
        modifier = modifier,
        state = state,
        listState = listState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSearchConfirmed = viewModel::confirmQuery,
        onHistoryItemClicked = { query ->
            viewModel.onSearchQueryChanged(query)
            viewModel.confirmQuery(query)
        },
        onHistoryItemDeleted = viewModel::removeHistoryItem,
        onItemClicked = { releaseId ->
            if (state.searchQuery.isNotBlank()) {
                viewModel.confirmQuery(state.searchQuery)
            }
            navController.navigate("details_screen/$releaseId")
        },
        onErrorShown = viewModel::errorHasShown,
        onToggleFavorite = viewModel::onToggleFavorite,
        onClearHistory = viewModel::clearHistory,
        onLoadMore = viewModel::loadMore,
    )
}

@Composable
fun FavoritesScreenContent(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    navController: NavController,
    onScrollToTopProvider: ((() -> Unit)) -> Unit = {},
) {
    val viewModel: FavoritesViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    var showSettings by remember { mutableStateOf(false) }

    val scrollToTop: () -> Unit = { listState.requestScrollToItem(0) }
    val currentScrollToTop by rememberUpdatedState(scrollToTop)
    SideEffect {
        onScrollToTopProvider(currentScrollToTop)
    }

    if (showSettings) {
        FavoriteSortBottomSheet(
            currentMode = state.currentSortMode,
            onDismiss = { showSettings = false },
            onSelect = { mode ->
                showSettings = false
                viewModel.setSortMode(mode)
                scrollToTop()
            },
        )
    }

    FavoritesScreen(
        modifier = modifier,
        state = state,
        listState = listState,
        onRemoveFavorite = viewModel::onRemoveFavorite,
        onItemClicked = { releaseId ->
            navController.navigate("details_screen/$releaseId")
        },
        onSettingsClicked = { navController.navigate(ScreenRoute.Settings.route) },
        onSortClicked = { showSettings = true },
        onLoadMore = viewModel::loadMore,
        onGenreClicked = { genre ->
            viewModel.setGenreFilter(genre)
            scrollToTop()
        },
    )
}

@Composable
fun DetailsScreenContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val viewModel: ReleaseDetailsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    ) { innerPadding ->
        ReleaseDetailsScreen(
            modifier = modifier.padding(innerPadding),
            state = state,
            onToggleFavorite = viewModel::onToggleFavorite,
            onRetry = viewModel::retry,
            onShare = {
                val details = state.detailsState
                val shareText = "https://discogs.com/release/${details.id}"
                val shareTitle = context.getString(R.string.share_via)
                val messengerIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        addCategory(Intent.CATEGORY_APP_MESSAGING)
                    }
                } else {
                    null
                }
                val intent = if (messengerIntent != null
                    && context.packageManager.queryIntentActivities(messengerIntent, 0).isNotEmpty()
                ) {
                    Intent.createChooser(messengerIntent, shareTitle)
                } else {
                    val generalIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    Intent.createChooser(generalIntent, shareTitle)
                }
                context.startActivity(intent)
            },
        )
    }
}

@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        modifier = modifier,
        state = state,
        onBack = onBack,
        onThemeModeChanged = viewModel::setThemeMode,
    )
}
