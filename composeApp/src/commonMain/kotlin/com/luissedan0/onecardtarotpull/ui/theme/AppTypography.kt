package com.luissedan0.onecardtarotpull.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import onecardtarotpull.composeapp.generated.resources.Res
import onecardtarotpull.composeapp.generated.resources.cinzel_variable
import onecardtarotpull.composeapp.generated.resources.nunito_variable
import org.jetbrains.compose.resources.Font

/**
 * Composable that builds [FontFamily]s from the bundled Compose Resources fonts:
 *
 * - **Cinzel** (variable, 400–900) — Roman serif designed for display text.
 *   Used for: card names, major arcana headings, app title.
 *   Source: [Google Fonts — Cinzel](https://fonts.google.com/specimen/Cinzel) (OFL-1.1)
 *
 * - **Nunito** (variable, 200–900) — Rounded sans-serif, highly legible body copy.
 *   Used for: card meanings, keywords, labels, navigation.
 *   Source: [Google Fonts — Nunito](https://fonts.google.com/specimen/Nunito) (OFL-1.1)
 */
@Composable
private fun cinzelFamily(): FontFamily = FontFamily(
    Font(resource = Res.font.cinzel_variable, weight = FontWeight.Normal),
    Font(resource = Res.font.cinzel_variable, weight = FontWeight.Medium),
    Font(resource = Res.font.cinzel_variable, weight = FontWeight.SemiBold),
    Font(resource = Res.font.cinzel_variable, weight = FontWeight.Bold),
    Font(resource = Res.font.cinzel_variable, weight = FontWeight.ExtraBold),
)

@Composable
private fun nunitoFamily(): FontFamily = FontFamily(
    Font(resource = Res.font.nunito_variable, weight = FontWeight.Normal),
    Font(resource = Res.font.nunito_variable, weight = FontWeight.Medium),
    Font(resource = Res.font.nunito_variable, weight = FontWeight.SemiBold),
    Font(resource = Res.font.nunito_variable, weight = FontWeight.Bold),
)

/**
 * Builds the app's [Typography] using the two custom font families.
 *
 * Scale rationale for a tarot reading app:
 * - **Display / Headline** → Cinzel: evokes stone carvings and ancient manuscripts;
 *   perfect for card names, section headers, and the pull-result title.
 * - **Title / Label / Body** → Nunito: rounded and friendly for reading long
 *   interpretations without fatigue; excellent small-size legibility.
 *
 * Called once inside [AppTheme]; consumers use [MaterialTheme.typography.*] directly.
 */
@Composable
fun appTypography(): Typography {
    val cinzel = cinzelFamily()
    val nunito = nunitoFamily()

    return Typography(
        // ── Display ──────────────────────────────────────────────────────────
        // Used for: the pulled card's name on the Home/Pull screen.
        displayLarge = TextStyle(
            fontFamily = cinzel,
            fontWeight = FontWeight.Bold,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = cinzel,
            fontWeight = FontWeight.SemiBold,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = cinzel,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),

        // ── Headline ─────────────────────────────────────────────────────────
        // Used for: section headings in the Details screen (Keywords, Meaning).
        headlineLarge = TextStyle(
            fontFamily = cinzel,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = cinzel,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = cinzel,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),

        // ── Title ─────────────────────────────────────────────────────────────
        // Used for: TopAppBar title, card suit label, dialog headings.
        titleLarge = TextStyle(
            fontFamily = cinzel,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),

        // ── Body ──────────────────────────────────────────────────────────────
        // Used for: card meaning text, journal notes, settings descriptions.
        bodyLarge = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),

        // ── Label ─────────────────────────────────────────────────────────────
        // Used for: bottom nav labels, keyword chips, timestamps.
        labelLarge = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
}
