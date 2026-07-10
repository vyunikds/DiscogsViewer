package com.example.discogsviewer.tests

import androidx.compose.ui.test.onAllNodesWithTag
import androidx.test.filters.LargeTest
import com.example.database.dbo.CountryDbo
import com.example.database.dbo.FavoriteDbo
import com.example.database.dbo.GenreDbo
import com.example.database.dbo.ReleaseCountryDbo
import com.example.database.dbo.ReleaseDbo
import com.example.database.dbo.ReleaseGenreDbo
import com.example.database.dbo.TopReleaseDbo
import com.example.discogsviewer.BaseKaspressoTest
import com.example.discogsviewer.screens.BottomBarPO
import com.example.discogsviewer.screens.FavoritesScreenPO
import com.example.discogsviewer.screens.ReleasesScreenPO
import com.example.discogsviewer.screens.SearchScreenPO
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@LargeTest
@HiltAndroidTest
class CompleteNavigationJourneyTest : BaseKaspressoTest() {

    private val releaseId = "1"

    @Before
    fun setupTestData() {
        runBlocking {
            releaseDao.insertReleases(listOf(
                ReleaseDbo(
                    id = releaseId,
                    artistTitle = "Pink Floyd",
                    releaseTitle = "The Dark Side of the Moon",
                    thumb = "",
                    coverImage = "",
                    communityHave = 5000,
                    communityWant = 3000,
                ),
            ))
            releaseDao.insertGenres(listOf(GenreDbo("Progressive Rock")))
            releaseDao.insertReleaseGenres(listOf(ReleaseGenreDbo(releaseId, "Progressive Rock")))
            releaseDao.insertCountries(listOf(CountryDbo("UK")))
            releaseDao.insertReleaseCountries(listOf(ReleaseCountryDbo(releaseId, "UK")))
            topReleaseDao.insertAll(listOf(TopReleaseDbo(releaseId)))
            favoritesDao.insert(FavoriteDbo(releaseId, System.currentTimeMillis()))
        }
        launchActivity()
    }

    @Test
    fun bottomNavigation_navigateBetweenScreens() = run {
        step("Start on releases screen") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("releases_screen", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }

            withScreen<ReleasesScreenPO> {
                flakySafely { releaseCard(releaseId).assertIsDisplayed() }
            }
        }

        step("Navigate to search via bottom nav") {
            withScreen<BottomBarPO> {
                searchNavItem.performClick()
            }

            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("search_screen", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }

            withScreen<SearchScreenPO> {
                flakySafely { searchField.assertIsDisplayed() }
            }
        }

        step("Navigate to favorites via bottom nav") {
            withScreen<BottomBarPO> {
                favoritesNavItem.performClick()
            }

            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("favorites_screen", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }

            withScreen<FavoritesScreenPO> {
                flakySafely { card(releaseId).assertIsDisplayed() }
            }
        }

        step("Navigate back to releases via bottom nav") {
            withScreen<BottomBarPO> {
                releasesNavItem.performClick()
            }

            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("releases_screen", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }

            withScreen<ReleasesScreenPO> {
                flakySafely { releaseCard(releaseId).assertIsDisplayed() }
            }
        }
    }
}
