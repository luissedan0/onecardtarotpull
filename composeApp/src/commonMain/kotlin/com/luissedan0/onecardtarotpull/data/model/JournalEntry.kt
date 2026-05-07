package com.luissedan0.onecardtarotpull.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A persisted record of a tarot card pull, stored in the local Room database.
 *
 * @param id          Auto-generated primary key.
 * @param cardId      References [TarotCard.id] so the Details screen can reload full card data.
 * @param cardDisplayName The human-readable label at pull time (e.g. "The Fool (Reversed)").
 *                    Stored directly so journal entries remain readable even if the deck data
 *                    were ever updated.
 * @param isReversed  Whether the card was reversed when pulled.
 * @param timestamp   Unix epoch milliseconds — used for sorting (newest first) and display.
 */
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val cardId: Int,
    val cardDisplayName: String,
    val isReversed: Boolean,
    val timestamp: Long
)
