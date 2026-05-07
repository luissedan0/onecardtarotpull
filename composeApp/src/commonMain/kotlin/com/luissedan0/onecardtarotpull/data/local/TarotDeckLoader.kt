package com.luissedan0.onecardtarotpull.data.local

import com.luissedan0.onecardtarotpull.data.model.TarotCard
import kotlinx.serialization.json.Json
import onecardtarotpull.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Loads the full 78-card tarot deck from the bundled [Res.files.tarot_deck] JSON resource.
 *
 * The result is cached in memory after the first load — [load] is safe to call multiple times.
 *
 * Usage:
 * ```kotlin
 * val deck: List<TarotCard> = TarotDeckLoader.load()
 * ```
 */
object TarotDeckLoader {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @kotlin.concurrent.Volatile
    private var cache: List<TarotCard>? = null

    /**
     * Returns the full deck. Reads and parses the JSON resource on first call,
     * then serves the in-memory cache on subsequent calls.
     *
     * Must be called from a coroutine context as [Res.readBytes] is a suspend function.
     */
    @OptIn(ExperimentalResourceApi::class)
    suspend fun load(): List<TarotCard> {
        cache?.let { return it }
        val bytes = Res.readBytes("files/tarot_deck.json")
        val text = bytes.decodeToString()
        val parsed = json.decodeFromString<List<TarotCard>>(text)
        cache = parsed
        return parsed
    }

    /** Clears the in-memory cache (useful for testing). */
    fun clearCache() {
        cache = null
    }
}
