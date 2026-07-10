package com.example.discogsviewer

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.database.AppDatabase
import com.example.database.dao.FavoritesDao
import com.example.database.dao.ReleaseDao
import com.example.database.dao.TopReleaseDao
import com.example.settings.SearchHistoryRepository
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import javax.inject.Inject

@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
abstract class BaseKaspressoTest : TestCase(
    Kaspresso.Builder.withComposeSupport(),
) {
    companion object {
        const val COMPOSE_RECOMPOSITION_TIMEOUT_MS = 5_000L
    }

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule: ComposeTestRule = createEmptyComposeRule()

    @Inject lateinit var releaseDao: ReleaseDao
    @Inject lateinit var favoritesDao: FavoritesDao
    @Inject lateinit var topReleaseDao: TopReleaseDao
    @Inject lateinit var database: AppDatabase
    @Inject lateinit var searchHistoryRepository: SearchHistoryRepository

    private var activityScenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setup() {
        hiltRule.inject()
    }

    protected fun launchActivity() {
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }

    protected fun seedTestData(
        block: suspend (ReleaseDao, FavoritesDao, TopReleaseDao) -> Unit,
    ) {
        runBlocking {
            block(releaseDao, favoritesDao, topReleaseDao)
        }
    }

    protected inline fun <reified T : ComposeScreen<T>> withScreen(
        noinline block: T.() -> Unit,
    ) {
        composeTestRule.waitForIdle()
        ComposeScreen.onComposeScreen<T>(composeTestRule, block)
    }

    @After
    fun tearDown() {
        runBlocking {
            database.clearAllTables()
        }
        activityScenario?.close()
        activityScenario = null
    }
}
