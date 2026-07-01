package com.example.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5: Migration = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE top_releases (
                releaseId TEXT NOT NULL PRIMARY KEY
            )
            """.trimIndent()
        )
    }
}
