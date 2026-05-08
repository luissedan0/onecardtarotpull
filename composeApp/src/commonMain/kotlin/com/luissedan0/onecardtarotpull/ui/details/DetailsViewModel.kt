package com.luissedan0.onecardtarotpull.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luissedan0.onecardtarotpull.data.model.CardMeaning
import com.luissedan0.onecardtarotpull.domain.usecase.GetCardMeaningUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for [DetailsScreen].
 *
 * Loads the [CardMeaning] for the given [cardId] once and exposes it as a [StateFlow].
 * The null initial value serves as the loading indicator; the screen switches to
 * content once the meaning resolves.
 *
 * `cardId` and `isReversed` originate from the typed [AppRoutes.Details] nav route,
 * extracted in [DetailsScreen] via `backStackEntry.toRoute<AppRoutes.Details>()` and
 * forwarded to this ViewModel via Koin `parametersOf`. This satisfies checklist item 13.3.
 *
 * Injected by Koin — see `AppModule.kt`:
 * ```kotlin
 * viewModel { params ->
 *     DetailsViewModel(params.get(), params.get(), get())
 * }
 * ```
 * Called from the composable as:
 * ```kotlin
 * koinViewModel(parameters = { parametersOf(cardId, isReversed) })
 * ```
 *
 * @param cardId               The [TarotCard.id] whose meaning to load.
 * @param isReversed           Whether the card was pulled reversed — stored verbatim, not
 *                             re-derived from the repository.
 * @param getCardMeaningUseCase Suspend function returning [CardMeaning] or `null`.
 */
class DetailsViewModel(
    val cardId: Int,
    val isReversed: Boolean,
    private val getCardMeaningUseCase: GetCardMeaningUseCase
) : ViewModel() {

    /**
     * The loaded card meaning, or `null` while loading (initial value = loading state).
     *
     * A cold [kotlinx.coroutines.flow.flow] wraps the suspend use-case call so that:
     * - The composable can distinguish "still loading" (`null`) from "loaded" (non-null).
     * - The coroutine is cancelled automatically if the NavBackStackEntry is popped before
     *   the IO finishes (via [SharingStarted.WhileSubscribed]).
     */
    val cardMeaning: StateFlow<CardMeaning?> = flow {
        emit(getCardMeaningUseCase(cardId))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )
}
