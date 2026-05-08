package com.luissedan0.onecardtarotpull.di

import com.luissedan0.onecardtarotpull.data.local.SettingsDataStore
import com.luissedan0.onecardtarotpull.data.local.TarotDatabase
import com.luissedan0.onecardtarotpull.data.local.createDataStore
import com.luissedan0.onecardtarotpull.data.local.createTarotDatabase
import com.luissedan0.onecardtarotpull.data.repository.CardMeaningRepository
import com.luissedan0.onecardtarotpull.data.repository.CardMeaningRepositoryStrategy
import com.luissedan0.onecardtarotpull.data.repository.JournalRepository
import com.luissedan0.onecardtarotpull.data.repository.JournalRepositoryImpl
import com.luissedan0.onecardtarotpull.data.repository.LocalCardMeaningRepository
import com.luissedan0.onecardtarotpull.data.repository.RemoteCardMeaningRepository
import com.luissedan0.onecardtarotpull.data.repository.SettingsRepository
import com.luissedan0.onecardtarotpull.data.repository.SettingsRepositoryImpl
import com.luissedan0.onecardtarotpull.data.repository.TarotDeckRepository
import com.luissedan0.onecardtarotpull.data.repository.TarotDeckRepositoryImpl
import com.luissedan0.onecardtarotpull.domain.usecase.DeleteJournalEntryUseCase
import com.luissedan0.onecardtarotpull.domain.usecase.GetCardMeaningUseCase
import com.luissedan0.onecardtarotpull.domain.usecase.GetJournalEntriesUseCase
import com.luissedan0.onecardtarotpull.domain.usecase.GetSettingsUseCase
import com.luissedan0.onecardtarotpull.domain.usecase.PullCardUseCase
import com.luissedan0.onecardtarotpull.domain.usecase.SaveJournalEntryUseCase
import com.luissedan0.onecardtarotpull.domain.usecase.UpdateSettingsUseCase
import com.luissedan0.onecardtarotpull.ui.home.HomeViewModel
import com.luissedan0.onecardtarotpull.ui.journal.JournalViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for all platform-agnostic singletons and use cases.
 *
 * Platform-specific bindings (HapticFeedback, ImagePicker, ShareHandler) are provided
 * by [androidModule] and [iosModule] and are NOT declared here.
 *
 * Dependency resolution order (bottom-up):
 *  DB / DataStore → DAOs / DataStore wrappers → Repositories → Use cases
 */
val appModule = module {

    // ── Persistence layer ─────────────────────────────────────────────────────

    /** Room database — single instance for the app lifetime. */
    single { createTarotDatabase() }

    /** JournalDao extracted from the singleton TarotDatabase. */
    single { get<TarotDatabase>().journalDao() }

    /** DataStore<Preferences> — single instance for the app lifetime. */
    single { createDataStore() }

    /** Typed wrapper around DataStore<Preferences>. */
    single { SettingsDataStore(get()) }

    // ── Repository layer ──────────────────────────────────────────────────────

    single<TarotDeckRepository> { TarotDeckRepositoryImpl() }

    /** Local card meanings — reads from the bundled tarot_deck.json resource. */
    single { LocalCardMeaningRepository() }

    /** Remote card meanings — stub until an API endpoint is chosen (Phase 4.5). */
    single { RemoteCardMeaningRepository() }

    /**
     * Active card-meaning source — local-first with optional remote fallback.
     * Set [CardMeaningRepositoryStrategy.useRemote] = true once an API is ready.
     */
    single<CardMeaningRepository> { CardMeaningRepositoryStrategy(get(), get()) }

    single<JournalRepository> { JournalRepositoryImpl(get()) }

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    // ── Use cases ─────────────────────────────────────────────────────────────
    // factory {} = new instance per injection site (use cases are stateless).

    factory { PullCardUseCase(get()) }
    factory { SaveJournalEntryUseCase(get()) }
    factory { GetJournalEntriesUseCase(get()) }
    factory { DeleteJournalEntryUseCase(get()) }
    factory { GetCardMeaningUseCase(get()) }
    factory { GetSettingsUseCase(get()) }
    factory { UpdateSettingsUseCase(get()) }

    // ── ViewModels ────────────────────────────────────────────────────────────

    /**
     * [HomeViewModel] — pull-card state machine, auto-save observation, haptic feedback.
     *
     * `HapticFeedback` is resolved from the platform module (androidModule / iosModule).
     * All use-case `get()` calls resolve from the factory bindings above.
     */
    viewModel { HomeViewModel(get(), get(), get(), get()) }

    /** [JournalViewModel] — live Room list + delete action. */
    viewModel { JournalViewModel(get(), get()) }

    // SettingsViewModel → Phase 12 / 13
    // DetailsViewModel  → Phase 12
}
