package com.example.discogsviewer.tests

import androidx.compose.ui.test.onAllNodesWithTag
import androidx.test.filters.LargeTest
import com.example.discogsviewer.BaseKaspressoTest
import com.example.discogsviewer.screens.BottomBarPO
import com.example.discogsviewer.screens.SearchScreenPO
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@LargeTest
@HiltAndroidTest
class SearchScreenTest : BaseKaspressoTest() {

    @Before
    fun setupTestData() {
        launchActivity()
    }

    @Test
    fun searchScreen_displaysEmptyHistory() = run {
        step("Navigate to search screen") {
            withScreen<BottomBarPO> {
                flakySafely { searchNavItem.performClick() }
            }
        }

        step("Empty history text displayed") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("search_empty", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }

            withScreen<SearchScreenPO> {
                flakySafely { emptyHistoryText.assertIsDisplayed() }
            }
        }
    }

    @Test
    fun searchScreen_displaysWithHistory() = run {
        step("Seed search history data") {
            runBlocking {
                searchHistoryRepository.clearHistory()
                searchHistoryRepository.addQuery("Pink Floyd")
                searchHistoryRepository.addQuery("Metallica")
            }
        }

        step("Navigate to search screen") {
            withScreen<BottomBarPO> {
                flakySafely { searchNavItem.performClick() }
            }
        }

        step("Wait for search screen to appear") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("search_screen", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }

        step("Search field is displayed") {
            withScreen<SearchScreenPO> {
                flakySafely { searchField.assertIsDisplayed() }
            }
        }

        step("History items are displayed") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("search_history_item", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }

            withScreen<SearchScreenPO> {
                flakySafely { historyItem.assertIsDisplayed() }
            }
        }
    }

    @Test
    fun searchScreen_deleteHistoryItem() = run {
        step("Seed search history data") {
            runBlocking {
                searchHistoryRepository.clearHistory()
                searchHistoryRepository.addQuery("Pink Floyd")
            }
        }

        step("Navigate to search screen") {
            withScreen<BottomBarPO> {
                flakySafely { searchNavItem.performClick() }
            }
        }

        step("Wait for history item to appear") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("search_history_item", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }

        step("Delete history item") {
            withScreen<SearchScreenPO> {
                flakySafely { historyDeleteButton.performClick() }
            }
        }

        step("Empty history message appears") {
            composeTestRule.waitUntil(timeoutMillis = COMPOSE_RECOMPOSITION_TIMEOUT_MS) {
                composeTestRule.onAllNodesWithTag("search_empty", useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }

            withScreen<SearchScreenPO> {
                flakySafely { emptyHistoryText.assertIsDisplayed() }
            }
        }
    }
}
