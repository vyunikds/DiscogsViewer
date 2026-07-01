package com.example.discogsviewer.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.core.basepresentation.ScreenRouter
import com.example.discogsviewer.ScreenRoute
import com.example.feature.details.navigation.DetailsScreenRoute
import com.example.feature.favorites.navigation.FavoritesScreenRoute
import com.example.feature.releases.navigation.ReleasesScreenRoute
import com.example.feature.search.navigation.SearchScreenRoute
import com.example.feature.settings.navigation.SettingsScreenRoute

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

                ReleasesScreenRoute(
                    listState = listState,
                    onItemClicked = { releaseId ->
                        navController.navigate(ScreenRouter.detailsRoute(releaseId))
                    },
                    onScrollToTopProvider = { callback ->
                        scrollCallbacks[ScreenRoute.TopReleases.route] = callback
                    }
                )
            }
            composable(
                route = ScreenRoute.Details.route,
                arguments = listOf(navArgument("releaseId") {
                    type = NavType.StringType
                }),
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
                DetailsScreenRoute(
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
                SearchScreenRoute(
                    listState = listState,
                    onItemClicked = { releaseId ->
                        navController.navigate(ScreenRouter.detailsRoute(releaseId))
                    },
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

                FavoritesScreenRoute(
                    listState = listState,
                    onItemClicked = { releaseId ->
                        navController.navigate(ScreenRouter.detailsRoute(releaseId))
                    },
                    onSettingsClicked = { navController.navigate(ScreenRoute.Settings.route) },
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
                SettingsScreenRoute(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
