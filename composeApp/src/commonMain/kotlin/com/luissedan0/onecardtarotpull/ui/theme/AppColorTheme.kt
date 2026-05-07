package com.luissedan0.onecardtarotpull.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

/**
 * The set of visual themes available in the app.
 *
 * Both palettes are dark-mode-only (tarot apps are inherently nocturnal).
 *
 * The active theme is stored by name in [DataStore] (via [SettingsDataStore.colorThemeName]),
 * observed by [SettingsViewModel], and threaded down to [AppTheme] through [App].
 *
 * ### Adding a new palette
 * 1. Add an entry to this enum.
 * 2. Add a `private val <name>Colors` constant below.
 * 3. Add the mapping in [colorScheme].
 */
enum class AppColorTheme(val displayName: String) {
    /** Deep navy + antique gold + warm cream. The default mystical tarot look. */
    Mystical(displayName = "Mystical"),

    /** Near-black + blood red + bone white. Fire and shadows. */
    Inferno(displayName = "Inferno");

    /**
     * Returns the [ColorScheme] for this theme.
     * Called once inside [AppTheme] on every recomposition where the theme changes.
     */
    val colorScheme: ColorScheme
        get() = when (this) {
            Mystical -> mysticalColorScheme
            Inferno -> infernoColorScheme
        }

    companion object {
        /** Convert a stored string name back to an [AppColorTheme], defaulting to [Mystical]. */
        fun fromName(name: String?): AppColorTheme =
            entries.firstOrNull { it.name == name } ?: Mystical
    }
}

// ─── Mystical Palette ─────────────────────────────────────────────────────────
// Deep midnight navy, antique gold accents, warm cream text on dark backgrounds.

private val mysticalBackground      = Color(0xFF0D1B2A) // darkest navy — the night sky
private val mysticalSurface         = Color(0xFF162236) // slightly lighter navy — cards/panels
private val mysticalSurfaceVariant  = Color(0xFF1E3050) // mid navy — chip / input backgrounds
private val mysticalSurfaceContainer= Color(0xFF1A2D43) // for container surfaces
private val mysticalOnSurfaceVariant= Color(0xFFAEC4DE) // muted sky blue — labels in variant areas
private val mysticalPrimary         = Color(0xFFC9A84C) // antique gold — CTA, FAB, active icons
private val mysticalOnPrimary       = Color(0xFF0D1B2A) // dark navy on gold buttons
private val mysticalPrimaryContainer= Color(0xFF3B2A00) // dark gold-brown — chip backgrounds
private val mysticalOnPrimaryContainer = Color(0xFFFFDC85)
private val mysticalSecondary       = Color(0xFF8FB4D8) // pale cerulean — secondary actions
private val mysticalOnSecondary     = Color(0xFF0D1B2A)
private val mysticalSecondaryContainer = Color(0xFF1A3550)
private val mysticalOnSecondaryContainer = Color(0xFFCDE5FF)
private val mysticalTertiary        = Color(0xFF9B72CF) // mystic amethyst — accents
private val mysticalOnTertiary      = Color(0xFF0D1B2A)
private val mysticalTertiaryContainer = Color(0xFF2D1060)
private val mysticalOnTertiaryContainer = Color(0xFFE9DDFF)
private val mysticalOnBackground    = Color(0xFFE8E0D0) // warm cream — primary text
private val mysticalOnSurface       = Color(0xFFE8E0D0) // warm cream — text on panels
private val mysticalOutline         = Color(0xFF4A6080) // muted navy-grey — dividers
private val mysticalOutlineVariant  = Color(0xFF2A4060) // subtler dividers
private val mysticalError           = Color(0xFFCF6679)
private val mysticalOnError         = Color(0xFF1A0010)
private val mysticalErrorContainer  = Color(0xFF5A0020)
private val mysticalOnErrorContainer= Color(0xFFFFD9DE)
private val mysticalInverseSurface  = Color(0xFFE8E0D0)
private val mysticalInverseOnSurface= Color(0xFF0D1B2A)
private val mysticalInversePrimary  = Color(0xFF7A5F20)
private val mysticalScrim           = Color(0xFF000A14)
private val mysticalShadow          = Color(0xFF000A14)

val mysticalColorScheme: ColorScheme = darkColorScheme(
    primary = mysticalPrimary,
    onPrimary = mysticalOnPrimary,
    primaryContainer = mysticalPrimaryContainer,
    onPrimaryContainer = mysticalOnPrimaryContainer,
    secondary = mysticalSecondary,
    onSecondary = mysticalOnSecondary,
    secondaryContainer = mysticalSecondaryContainer,
    onSecondaryContainer = mysticalOnSecondaryContainer,
    tertiary = mysticalTertiary,
    onTertiary = mysticalOnTertiary,
    tertiaryContainer = mysticalTertiaryContainer,
    onTertiaryContainer = mysticalOnTertiaryContainer,
    error = mysticalError,
    onError = mysticalOnError,
    errorContainer = mysticalErrorContainer,
    onErrorContainer = mysticalOnErrorContainer,
    background = mysticalBackground,
    onBackground = mysticalOnBackground,
    surface = mysticalSurface,
    onSurface = mysticalOnSurface,
    surfaceVariant = mysticalSurfaceVariant,
    onSurfaceVariant = mysticalOnSurfaceVariant,
    outline = mysticalOutline,
    outlineVariant = mysticalOutlineVariant,
    scrim = mysticalScrim,
    inverseSurface = mysticalInverseSurface,
    inverseOnSurface = mysticalInverseOnSurface,
    inversePrimary = mysticalInversePrimary,
    surfaceContainer = mysticalSurfaceContainer,
)

// ─── Inferno Palette ──────────────────────────────────────────────────────────
// Near-black void, blood-red flame, bone-white ash.
// Black: #0A0000  |  Red: #CC2200  |  Bone white: #F5F0E0

private val infernoBackground       = Color(0xFF0A0000) // near-black void — scorched earth
private val infernoSurface          = Color(0xFF1A0500) // very dark, red-tinged black
private val infernoSurfaceVariant   = Color(0xFF2D0A00) // dark ember
private val infernoSurfaceContainer = Color(0xFF250500)
private val infernoOnSurfaceVariant = Color(0xFFD4A090) // warm ash/terracotta
private val infernoPrimary          = Color(0xFFCC2200) // blood red — flame, danger
private val infernoOnPrimary        = Color(0xFFF5F0E0) // bone white on red
private val infernoPrimaryContainer = Color(0xFF6B1100) // dark crimson container
private val infernoOnPrimaryContainer = Color(0xFFFFB4A0)
private val infernoSecondary        = Color(0xFFE05020) // ember orange — heat shimmer
private val infernoOnSecondary      = Color(0xFF1A0500)
private val infernoSecondaryContainer = Color(0xFF5A1500)
private val infernoOnSecondaryContainer = Color(0xFFFFCBB8)
private val infernoTertiary         = Color(0xFFD4900A) // molten gold / amber
private val infernoOnTertiary       = Color(0xFF0A0000)
private val infernoTertiaryContainer= Color(0xFF5A3800)
private val infernoOnTertiaryContainer = Color(0xFFFFD880)
private val infernoOnBackground     = Color(0xFFF5F0E0) // bone white — ash and parchment
private val infernoOnSurface        = Color(0xFFF5F0E0) // bone white on panel
private val infernoOutline          = Color(0xFF7A2000) // dark red dividers
private val infernoOutlineVariant   = Color(0xFF4D1500)
private val infernoError            = Color(0xFFFF8070)
private val infernoOnError          = Color(0xFF1A0000)
private val infernoErrorContainer   = Color(0xFF400000)
private val infernoOnErrorContainer = Color(0xFFFFDAD6)
private val infernoInverseSurface   = Color(0xFFF5F0E0)
private val infernoInverseOnSurface = Color(0xFF0A0000)
private val infernoInversePrimary   = Color(0xFF8B3020)
private val infernoScrim            = Color(0xFF000000)
private val infernoShadow           = Color(0xFF000000)

val infernoColorScheme: ColorScheme = darkColorScheme(
    primary = infernoPrimary,
    onPrimary = infernoOnPrimary,
    primaryContainer = infernoPrimaryContainer,
    onPrimaryContainer = infernoOnPrimaryContainer,
    secondary = infernoSecondary,
    onSecondary = infernoOnSecondary,
    secondaryContainer = infernoSecondaryContainer,
    onSecondaryContainer = infernoOnSecondaryContainer,
    tertiary = infernoTertiary,
    onTertiary = infernoOnTertiary,
    tertiaryContainer = infernoTertiaryContainer,
    onTertiaryContainer = infernoOnTertiaryContainer,
    error = infernoError,
    onError = infernoOnError,
    errorContainer = infernoErrorContainer,
    onErrorContainer = infernoOnErrorContainer,
    background = infernoBackground,
    onBackground = infernoOnBackground,
    surface = infernoSurface,
    onSurface = infernoOnSurface,
    surfaceVariant = infernoSurfaceVariant,
    onSurfaceVariant = infernoOnSurfaceVariant,
    outline = infernoOutline,
    outlineVariant = infernoOutlineVariant,
    scrim = infernoScrim,
    inverseSurface = infernoInverseSurface,
    inverseOnSurface = infernoInverseOnSurface,
    inversePrimary = infernoInversePrimary,
    surfaceContainer = infernoSurfaceContainer,
)
