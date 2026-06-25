package com.example.discogsviewer.favorites.domain

import org.junit.Test
import kotlin.test.assertEquals

enum class TestFavoriteSortMode {
    BY_DATE,
    BY_ARTIST_TITLE,
    BY_RELEASE_TITLE,
}

enum class TestDataSourceSortMode {
    BY_DATE,
    BY_ARTIST_TITLE,
    BY_RELEASE_TITLE,
}

fun TestFavoriteSortMode.toDataSourceSortMode(): TestDataSourceSortMode =
    when (this) {
        TestFavoriteSortMode.BY_DATE -> TestDataSourceSortMode.BY_DATE
        TestFavoriteSortMode.BY_ARTIST_TITLE -> TestDataSourceSortMode.BY_ARTIST_TITLE
        TestFavoriteSortMode.BY_RELEASE_TITLE -> TestDataSourceSortMode.BY_RELEASE_TITLE
    }

class FavoriteSortModeTest {

    @Test
    fun `BY_DATE maps to DataSourceSortMode BY_DATE`() {
        val result = TestFavoriteSortMode.BY_DATE.toDataSourceSortMode()

        assertEquals(TestDataSourceSortMode.BY_DATE, result)
    }

    @Test
    fun `BY_ARTIST_TITLE maps to DataSourceSortMode BY_ARTIST_TITLE`() {
        val result = TestFavoriteSortMode.BY_ARTIST_TITLE.toDataSourceSortMode()

        assertEquals(TestDataSourceSortMode.BY_ARTIST_TITLE, result)
    }

    @Test
    fun `BY_RELEASE_TITLE maps to DataSourceSortMode BY_RELEASE_TITLE`() {
        val result = TestFavoriteSortMode.BY_RELEASE_TITLE.toDataSourceSortMode()

        assertEquals(TestDataSourceSortMode.BY_RELEASE_TITLE, result)
    }
}
