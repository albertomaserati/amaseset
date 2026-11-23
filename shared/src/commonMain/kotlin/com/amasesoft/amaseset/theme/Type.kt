package com.amasesoft.amaseset.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.amasesoft.amaseset.resources.Res
import com.amasesoft.amaseset.resources.barlow_condensed_bold
import com.amasesoft.amaseset.resources.barlow_condensed_medium
import com.amasesoft.amaseset.resources.barlow_condensed_semibold
import org.jetbrains.compose.resources.Font

/**
 * Barlow Condensed (SIL Open Font License 1.1, see `docs/licenses/BarlowCondensed-OFL.txt`):
 * numbers and a few display titles. Body text keeps the system font. Provided by [AppTheme].
 */
internal val LocalCondensedFontFamily = staticCompositionLocalOf<FontFamily> { FontFamily.Default }

@Composable
internal fun rememberCondensedFontFamily(): FontFamily {
    val medium = Font(Res.font.barlow_condensed_medium, FontWeight.Medium)
    val semiBold = Font(Res.font.barlow_condensed_semibold, FontWeight.SemiBold)
    val bold = Font(Res.font.barlow_condensed_bold, FontWeight.Bold)
    return remember(medium, semiBold, bold) { FontFamily(medium, semiBold, bold) }
}

val CondensedFontFamily: FontFamily
    @Composable @ReadOnlyComposable get() = LocalCondensedFontFamily.current

/** Tabular figures (`tnum`): digits keep the same width while a number changes. */
private const val TABULAR = "tnum"

/** Huge countdown digits; tabular figures keep the width stable while counting. */
val CountdownTextStyle: TextStyle
    @Composable @ReadOnlyComposable get() = TextStyle(
        fontFamily = CondensedFontFamily,
        fontSize = 112.sp,
        lineHeight = 112.sp,
        fontWeight = FontWeight.Bold,
        fontFeatureSettings = TABULAR,
    )

/**
 * Condensed tabular figures for any changing number, merged onto a typography style:
 * `MaterialTheme.typography.titleLarge.merge(TabularNumbers)`.
 */
val TabularNumbers: TextStyle
    @Composable @ReadOnlyComposable get() = TextStyle(
        fontFamily = CondensedFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontFeatureSettings = TABULAR,
    )

/** Text styles of the app that the Material type scale does not cover. */
object AppTextStyles {
    /** Big durations at the end of a row (Home) and totals. */
    val numberLarge: TextStyle
        @Composable @ReadOnlyComposable get() = TextStyle(
            fontFamily = CondensedFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 30.sp,
            lineHeight = 32.sp,
            fontFeatureSettings = TABULAR,
        )

    /** Values of the steppers. */
    val numberMedium: TextStyle
        @Composable @ReadOnlyComposable get() = TextStyle(
            fontFamily = CondensedFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 32.sp,
            fontFeatureSettings = TABULAR,
        )

    /** Numbers in table columns (sets×reps, weights) and compact summaries. */
    val numberSmall: TextStyle
        @Composable @ReadOnlyComposable get() = TextStyle(
            fontFamily = CondensedFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontFeatureSettings = TABULAR,
        )

    /** Condensed uppercase title: the app name on the Home, the routine in the dropdown. */
    val condensedTitle: TextStyle
        @Composable @ReadOnlyComposable get() = TextStyle(
            fontFamily = CondensedFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 30.sp,
            letterSpacing = 1.sp,
        )

    /** Condensed uppercase label (run screen: "CYCLE 1 / 8 · SET 1 / 1", "NEXT · REST 0:10"). */
    val condensedLabel: TextStyle
        @Composable @ReadOnlyComposable get() = TextStyle(
            fontFamily = CondensedFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 22.sp,
            letterSpacing = 1.2.sp,
            fontFeatureSettings = TABULAR,
        )

    /** Small spaced section label in the system font ("SAVED · 2/50", table headers). */
    val overline: TextStyle
        @Composable @ReadOnlyComposable get() = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 1.5.sp,
            fontFeatureSettings = TABULAR,
        )
}
