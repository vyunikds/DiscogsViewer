package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.database.dbo.FavoriteDbo
import com.example.database.dbo.FullReleaseDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Transaction
    @Query("SELECT * FROM releases WHERE release_id IN (SELECT releaseId FROM favorites)")
    fun getAllFull(): Flow<List<FullReleaseDbo>>

    @Query("SELECT COUNT(*) FROM favorites")
    fun getCount(): Flow<Int>

    @Transaction
    @Query("""
        SELECT r.* FROM releases r
        INNER JOIN favorites f ON r.release_id = f.releaseId
        ORDER BY f.added_at DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getPaginatedByDate(limit: Int, offset: Int): List<FullReleaseDbo>

    @Transaction
    @Query("""
        SELECT r.* FROM releases r
        INNER JOIN favorites f ON r.release_id = f.releaseId
        ORDER BY r.artist_title COLLATE NOCASE, r.release_title COLLATE NOCASE
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getPaginatedByArtistTitle(limit: Int, offset: Int): List<FullReleaseDbo>

    @Transaction
    @Query("""
        SELECT r.* FROM releases r
        INNER JOIN favorites f ON r.release_id = f.releaseId
        ORDER BY r.release_title COLLATE NOCASE, r.artist_title COLLATE NOCASE
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getPaginatedByReleaseTitle(limit: Int, offset: Int): List<FullReleaseDbo>

    @Query("SELECT releaseId FROM favorites")
    fun getReleaseIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteDbo)

    @Query("DELETE FROM favorites WHERE releaseId = :releaseId")
    suspend fun delete(releaseId: String)
}
