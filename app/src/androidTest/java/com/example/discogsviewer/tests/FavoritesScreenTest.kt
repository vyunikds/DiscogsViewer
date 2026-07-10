package com.example.discogsviewer.tests

import androidx.compose.ui.test.onAllNodesWithTag
import androidx.test.filters.LargeTest
import com.example.database.dbo.CountryDbo
import com.example.database.dbo.FavoriteDbo
import com.example.database.dbo.GenreDbo
import com.example.database.dbo.ReleaseCountryDbo
import com.example.database.dbo.ReleaseDbo
import com.example.database.dbo.ReleaseGenreDbo
import com.example.discogsviewer.BaseKaspressoTest
import com.example.discogsviewer.screens.BottomBarPO
import com.example.discogsviewer.screens.FavoritesScreenPO
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@LargeTest
@HiltAndroidTest
class FavoritesScreenTest : BaseKaspressoTest() {

    private val releaseId = "1"

    @Before
    fun setupTestData() {
        runBlocking {
            releaseDao.insertReleases(listOf(
                ReleaseDbo(
                    id = releaseId,
                    artistTitle = "Daft Punk",
                    releaseTitle = "Random Access Memories",
                    thumb = "",
                    coverImage = "",
                    communityHave = 1000,
                    communityWant = 500,
                ),
            ))
            releaseDao.insertGenres(listOf(GenreDbo("Electronic")))
            releaseDao.insertReleaseGenres(listOf(ReleaseGenreDbo(releaseId, "Electronic")))
            releaseDao.insertCountries(listOf(CountryDbo("France")))
            releaseDao.insertReleaseCountries(listOf(ReleaseCountryDbo(releaseId, "France")))
            favoritesDao.insert(FavoriteDbo(releaseId, System.currentTimeMillis()))
        }
        launchActivity()
    }

    @Test
    fun favoritesScreen_displaysFavoriteRelease() = run {
        step("Navigate to favorites") {
            withScreen<BottomBarPO> {
                flakySafely { favoritesNavItem.performClick() }
            }
        }

        step("Wait for favorites screen") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("favorites_screen", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }

        step("Favorite release displayed") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("small_card_$releaseId", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }

            withScreen<FavoritesScreenPO> {
                flakySafely { card(releaseId).assertIsDisplayed() }
            }
        }

        step("Release title and artist displayed correctly") {
            withScreen<FavoritesScreenPO> {
                cardTitle(releaseId).assertTextContains("Random Access Memories")
                cardArtist(releaseId).assertTextContains("Daft Punk")
            }
        }
    }

    @Test
    fun favoritesScreen_removeFavorite() = run {
        step("Navigate to favorites") {
            withScreen<BottomBarPO> {
                flakySafely { favoritesNavItem.performClick() }
            }
        }

        step("Wait for card to appear") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("small_card_$releaseId", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }

        step("Remove favorite") {
            withScreen<FavoritesScreenPO> {
                flakySafely { removeFavoriteButton(releaseId).performClick() }
            }
        }

        step("Card disappears") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("small_card_$releaseId", useUnmergedTree = true)
                    .fetchSemanticsNodes().isEmpty()
            }
        }
    }
}
