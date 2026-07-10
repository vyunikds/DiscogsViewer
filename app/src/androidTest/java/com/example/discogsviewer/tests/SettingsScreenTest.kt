package com.example.discogsviewer.tests

import androidx.compose.ui.test.onAllNodesWithTag
import androidx.test.filters.LargeTest
import com.example.discogsviewer.BaseKaspressoTest
import com.example.discogsviewer.screens.BottomBarPO
import com.example.discogsviewer.screens.FavoritesScreenPO
import com.example.discogsviewer.screens.SettingsScreenPO
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test

@LargeTest
@HiltAndroidTest
class SettingsScreenTest : BaseKaspressoTest() {

    @Before
    fun setupTestData() {
        launchActivity()
    }

    @Test
    fun settingsScreen_displayedAndBackWorks() = run {
        step("Navigate to favorites") {
            withScreen<BottomBarPO> {
                flakySafely { favoritesNavItem.performClick() }
            }
        }

        step("Wait for favorites screen to appear") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("favorites_screen", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }

        step("Click settings button on favorites screen") {
            withScreen<FavoritesScreenPO> {
                flakySafely { settingsButton.performClick() }
            }
        }

        step("Press back to return from settings") {
            withScreen<SettingsScreenPO> {
                flakySafely { backButton.performClick() }
            }

            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("favorites_screen", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }
    }
}
