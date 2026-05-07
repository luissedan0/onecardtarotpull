package com.luissedan0.onecardtarotpull.data.repository

import com.luissedan0.onecardtarotpull.data.local.TarotDeckLoader
import com.luissedan0.onecardtarotpull.data.model.TarotCard

/**
 * Contract for accessing the full tarot deck.
 * Backed by [TarotDeckLoader] (bundled JSON resource) in production.
 */
interface TarotDeckRepository {
    /** Returns all 78 [TarotCard]s. Result is cached in memory after the first call. */
    suspend fun getAll(): List<TarotCard>

    /** Returns the card with the given [id], or `null` if no card matches. */
    suspend fun getById(id: Int): TarotCard?
}

class TarotDeckRepositoryImpl : TarotDeckRepository {

    override suspend fun getAll(): List<TarotCard> = TarotDeckLoader.load()

    override suspend fun getById(id: Int): TarotCard? =
        TarotDeckLoader.load().firstOrNull { it.id == id }
}
