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
import com.example.discogsviewer.screens.ReleasesScreenPO
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@LargeTest
@HiltAndroidTest
class ReleasesScreenTest : BaseKaspressoTest() {

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
    }

    @Test
    fun releasesScreen_displaysReleases() = run {
        step("Releases displayed in list") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("release_card_$releaseId", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }

            withScreen<ReleasesScreenPO> {
                flakySafely { releaseCard(releaseId).assertIsDisplayed() }
            }
        }

        step("Release title and artist displayed correctly") {
            withScreen<ReleasesScreenPO> {
                releaseTitle(releaseId).assertTextContains("The Dark Side of the Moon")
                releaseArtist(releaseId).assertTextContains("Pink Floyd")
            }
        }
    }

    @Test
    fun releasesScreen_favoriteToggle() = run {
        step("Favorite button is clickable") {
            withScreen<ReleasesScreenPO> {
                releaseFavorite(releaseId).performClick()
            }
        }
    }
}
