package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.dbo.FavoriteDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<FavoriteDbo>>

    @Query("SELECT COUNT(*) FROM favorites")
    fun getCount(): Flow<Int>

    @Query("SELECT * FROM favorites ORDER BY added_at DESC LIMIT :limit OFFSET :offset")
    suspend fun getPaginatedByDate(limit: Int, offset: Int): List<FavoriteDbo>

    @Query("SELECT * FROM favorites ORDER BY artist_title COLLATE NOCASE, release_title COLLATE NOCASE LIMIT :limit OFFSET :offset")
    suspend fun getPaginatedByArtistTitle(limit: Int, offset: Int): List<FavoriteDbo>

    @Query("SELECT * FROM favorites ORDER BY release_title COLLATE NOCASE, artist_title COLLATE NOCASE LIMIT :limit OFFSET :offset")
    suspend fun getPaginatedByReleaseTitle(limit: Int, offset: Int): List<FavoriteDbo>

    @Query("SELECT releaseId FROM favorites")
    fun getReleaseIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteDbo)

    @Query("DELETE FROM favorites WHERE releaseId = :releaseId")
    suspend fun delete(releaseId: String)
}