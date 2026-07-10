package com.example.discogsviewer.tests

import androidx.compose.ui.test.onAllNodesWithTag
import androidx.test.filters.LargeTest
import com.example.database.dbo.CountryDbo
import com.example.database.dbo.GenreDbo
import com.example.database.dbo.ReleaseCountryDbo
import com.example.database.dbo.ReleaseDbo
import com.example.database.dbo.ReleaseGenreDbo
import com.example.database.dbo.TopReleaseDbo
import com.example.discogsviewer.BaseKaspressoTest
import com.example.discogsviewer.screens.DetailsScreenPO
import com.example.discogsviewer.screens.ReleasesScreenPO
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@LargeTest
@HiltAndroidTest
    class DetailsScreenTest : BaseKaspressoTest() {

    private val releaseId = "1"

    @Before
    fun setupTestData() {
        runBlocking {
            releaseDao.insertReleases(
                listOf(
                    ReleaseDbo(
                        id = releaseId,
                        artistTitle = "Pink Floyd",
                        releaseTitle = "The Dark Side of the Moon",
                        thumb = "",
                        coverImage = "",
                        communityHave = 5000,
                        communityWant = 3000,
                    ),
                )
            )
            releaseDao.insertGenres(listOf(GenreDbo("Progressive Rock")))
            releaseDao.insertReleaseGenres(listOf(ReleaseGenreDbo(releaseId, "Progressive Rock")))
            releaseDao.insertCountries(listOf(CountryDbo("UK")))
            releaseDao.insertReleaseCountries(listOf(ReleaseCountryDbo(releaseId, "UK")))
            topReleaseDao.insertAll(listOf(TopReleaseDbo(releaseId)))
        }
        launchActivity()
        composeTestRule.waitForIdle()
    }

    @Test
    fun detailsScreen_navigateFromReleasesAndDisplayContent() = run {
        step("Wait for loading to finish") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("releases_loading", useUnmergedTree = true)
                    .fetchSemanticsNodes().isEmpty()
            }
        }

        step("Wait for release card to appear") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("release_card_$releaseId")
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }

        step("Click release card to open details") {
            withScreen<ReleasesScreenPO> {
                flakySafely { releaseCard(releaseId).performClick() }
            }
        }

        step("Details screen appears with correct title") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("details_screen", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }

            withScreen<DetailsScreenPO> {
                flakySafely { title.assertTextContains("The Dark Side of the Moon") }
            }
        }

        step("Share button is displayed") {
            withScreen<DetailsScreenPO> {
                flakySafely { shareButton.assertIsDisplayed() }
            }
        }
    }
}
