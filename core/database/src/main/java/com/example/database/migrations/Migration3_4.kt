package com.example.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val tempTable = """
            CREATE TABLE IF NOT EXISTS favorites_new (
                releaseId TEXT NOT NULL PRIMARY KEY,
                added_at INTEGER NOT NULL
            )
        """.trimIndent()

        val insert = """
            INSERT INTO favorites_new (releaseId, added_at)
            SELECT releaseId, added_at FROM favorites
        """.trimIndent()

        val drop = "DROP TABLE IF EXISTS favorites"

        val rename = "ALTER TABLE favorites_new RENAME TO favorites"

        database.execSQL(tempTable)
        database.execSQL(insert)
        database.execSQL(drop)
        database.execSQL(rename)
    }
}
