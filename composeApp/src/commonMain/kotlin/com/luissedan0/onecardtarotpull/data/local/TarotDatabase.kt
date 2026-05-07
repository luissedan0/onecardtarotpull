package com.luissedan0.onecardtarotpull.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.luissedan0.onecardtarotpull.data.model.JournalEntry

/**
 * Single Room database for the app.
 *
 * - Version 1 — initial schema with the [journal_entries] table.
 * - [exportSchema] = true writes the schema JSON to `composeApp/schemas/` (tracked in VCS).
 * - [ConstructedBy] is required by Room KMP for non-Android targets — Room's KSP processor
 *   replaces the `TODO` body with generated instantiation code.
 *
 * Platform-specific creation is handled via [createTarotDatabase] (expect/actual).
 */
@Database(
    entities = [JournalEntry::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(TarotDatabaseConstructor::class)
abstract class TarotDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
}

/**
 * KMP database constructor — the body is intentionally a placeholder.
 * Room's KSP annotation processor replaces it with the generated implementation at build time.
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object TarotDatabaseConstructor : RoomDatabaseConstructor<TarotDatabase> {
    override fun initialize(): TarotDatabase
}
