package com.luissedan0.onecardtarotpull

import com.luissedan0.onecardtarotpull.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for the [SettingsRepository] contract — 15.5.
 *
 * [SettingsRepositoryImpl] is a thin delegation wrapper over [SettingsDataStore].
 * To test the full contract without DataStore infra (file I/O, OkioStorage), this suite
 * uses [FakeSettingsRepository] which mirrors the same default values and flow semantics.
 *
 * The "real wiring" (DataStore ↔ repository) is covered by [SettingsViewModel] integration
 * tests in Phase 16 on a physical/emulator device.
 */
class SettingsRepositoryTest {

    // ── Fake ─────────────────────────────────────────────────────────────────

    private class FakeSettingsRepository : SettingsRepository {
        private val _autoSave = MutableStateFlow(false)
        private val _cardBackPath = MutableStateFlow<String?>(null)
        private val _colorTheme = MutableStateFlow<String?>(null)

        override val autoSaveEnabled: Flow<Boolean> = _autoSave
        override val customCardBackPath: Flow<String?> = _cardBackPath
        override val colorThemeName: Flow<String?> = _colorTheme

        override suspend fun setAutoSaveEnabled(enabled: Boolean) { _autoSave.value = enabled }
        override suspend fun setCustomCardBackPath(path: String?) { _cardBackPath.value = path }
        override suspend fun setColorThemeName(name: String) { _colorTheme.value = name }
    }

    // ── 15.5 tests ────────────────────────────────────────────────────────────

    @Test
    fun `autoSaveEnabled defaults to false`() = runTest {
        val repo = FakeSettingsRepository()
        assertFalse(repo.autoSaveEnabled.first())
    }

    @Test
    fun `setAutoSaveEnabled true reflects in flow`() = runTest {
        val repo = FakeSettingsRepository()
        repo.setAutoSaveEnabled(true)
        assertTrue(repo.autoSaveEnabled.first())
    }

    @Test
    fun `setAutoSaveEnabled can be toggled back to false`() = runTest {
        val repo = FakeSettingsRepository()
        repo.setAutoSaveEnabled(true)
        repo.setAutoSaveEnabled(false)
        assertFalse(repo.autoSaveEnabled.first())
    }

    @Test
    fun `customCardBackPath defaults to null`() = runTest {
        val repo = FakeSettingsRepository()
        assertNull(repo.customCardBackPath.first())
    }

    @Test
    fun `setCustomCardBackPath stores the path`() = runTest {
        val repo = FakeSettingsRepository()
        repo.setCustomCardBackPath("/data/user/0/com.example/files/card_back.jpg")
        assertEquals(
            "/data/user/0/com.example/files/card_back.jpg",
            repo.customCardBackPath.first()
        )
    }

    @Test
    fun `setCustomCardBackPath null clears the stored path`() = runTest {
        val repo = FakeSettingsRepository()
        repo.setCustomCardBackPath("/some/path.jpg")
        repo.setCustomCardBackPath(null)
        assertNull(repo.customCardBackPath.first())
    }

    @Test
    fun `colorThemeName defaults to null`() = runTest {
        val repo = FakeSettingsRepository()
        assertNull(repo.colorThemeName.first())
    }

    @Test
    fun `setColorThemeName stores the theme name`() = runTest {
        val repo = FakeSettingsRepository()
        repo.setColorThemeName("INFERNO")
        assertEquals("INFERNO", repo.colorThemeName.first())
    }

    @Test
    fun `setColorThemeName can be changed to a different theme`() = runTest {
        val repo = FakeSettingsRepository()
        repo.setColorThemeName("INFERNO")
        repo.setColorThemeName("MYSTICAL")
        assertEquals("MYSTICAL", repo.colorThemeName.first())
    }

    @Test
    fun `all three settings are independent of each other`() = runTest {
        val repo = FakeSettingsRepository()
        repo.setAutoSaveEnabled(true)
        repo.setCustomCardBackPath("/path.jpg")
        repo.setColorThemeName("INFERNO")

        assertTrue(repo.autoSaveEnabled.first())
        assertEquals("/path.jpg", repo.customCardBackPath.first())
        assertEquals("INFERNO", repo.colorThemeName.first())
    }
}
