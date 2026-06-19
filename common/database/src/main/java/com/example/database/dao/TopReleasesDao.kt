package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.dbo.TopReleasesDbo
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi

@Dao
interface TopReleasesDao {

    @OptIn(InternalSerializationApi::class)
    @Query("SELECT * FROM top_releases ORDER BY communityHave DESC")
    fun getAllReleases(): Flow<List<TopReleasesDbo>>

    @OptIn(InternalSerializationApi::class)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReleases(releases: List<TopReleasesDbo>)

    @Query("SELECT thumb FROM top_releases WHERE id = :releaseId")
    suspend fun getThumb(releaseId: Int): String?
}