package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.database.dbo.FullReleaseDbo
import com.example.database.dbo.TopReleaseDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface TopReleaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topReleases: List<TopReleaseDbo>)

    @Query("DELETE FROM top_releases")
    suspend fun clear()

    @Query("SELECT releaseId FROM top_releases")
    fun getReleaseIds(): Flow<List<String>>

    @Transaction
    @Query(
        """
        SELECT r.* FROM releases r
        INNER JOIN top_releases tr ON r.release_id = tr.releaseId
        ORDER BY r.community_have DESC
        """
    )
    fun getTopReleases(): Flow<List<FullReleaseDbo>>
}
