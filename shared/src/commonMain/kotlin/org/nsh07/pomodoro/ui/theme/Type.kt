/*
 * Copyright (c) 2025-2026 Nishant Mishra
 * Copyright (c) 2026 Mohammad (Focus Glass modifications)
 *
 * This file is part of Tomato - a minimalist pomodoro timer for Android.
 *
 * Tomato is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tomato is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tomato.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package org.nsh07.pomodoro.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import org.jetbrains.compose.resources.Font
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.google_sans_flex
import tomato.shared.generated.resources.vazirmatn_medium
import tomato.shared.generated.resources.vazirmatn_regular

val TYPOGRAPHY = Typography()

data class AppFonts(
    val topBarTitle: FontFamily,
    val unselectedTopicTitle: FontFamily,
    val annotatedString: FontFamily
)

/** Returns true when the current layout direction is RTL (e.g. Persian, Arabic). */
@Composable
fun isRtlLocale(): Boolean = LocalLayoutDirection.current == LayoutDirection.Rtl

@Composable
fun typography(): Typography {
    val isRtl = isRtlLocale()

    val vazirRegularFont = if (isRtl) Font(Res.font.vazirmatn_regular, FontWeight.Normal) else null
    val vazirMediumFont  = if (isRtl) Font(Res.font.vazirmatn_medium,  FontWeight.Bold)   else null
    val vazirFamily = if (vazirRegularFont != null && vazirMediumFont != null)
        remember(vazirRegularFont, vazirMediumFont) { FontFamily(vazirRegularFont, vazirMediumFont) }
    else null

    val googleFlex400Font = Font(
        Res.font.google_sans_flex,
        FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    )
    val googleFlex400 = remember(googleFlex400Font) { FontFamily(googleFlex400Font) }

    val googleFlex600Font = Font(
        Res.font.google_sans_flex,
        FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600),
            FontVariation.Setting("ROND", 100f)
        )
    )
    val googleFlex600 = remember(googleFlex600Font) { FontFamily(googleFlex600Font) }

    val normalFamily = vazirFamily ?: googleFlex400
    val boldFamily   = vazirFamily ?: googleFlex600

    return remember(normalFamily, boldFamily) {
        Typography(
            displayLarge = TYPOGRAPHY.displayLarge.copy(fontFamily = boldFamily),
            displayMedium = TYPOGRAPHY.displayMedium.copy(fontFamily = boldFamily),
            displaySmall = TYPOGRAPHY.displaySmall.copy(fontFamily = boldFamily),
            headlineLarge = TYPOGRAPHY.headlineLarge.copy(fontFamily = boldFamily),
            headlineMedium = TYPOGRAPHY.headlineMedium.copy(fontFamily = boldFamily),
            headlineSmall = TYPOGRAPHY.headlineSmall.copy(fontFamily = boldFamily),
            titleLarge = TYPOGRAPHY.titleLarge.copy(fontFamily = normalFamily),
            titleMedium = TYPOGRAPHY.titleMedium.copy(fontFamily = boldFamily),
            titleSmall = TYPOGRAPHY.titleSmall.copy(fontFamily = boldFamily),
            bodyLarge = TYPOGRAPHY.bodyLarge.copy(fontFamily = boldFamily),
            bodyMedium = TYPOGRAPHY.bodyMedium.copy(fontFamily = normalFamily),
            bodySmall = TYPOGRAPHY.bodySmall.copy(fontFamily = normalFamily),
            labelLarge = TYPOGRAPHY.labelLarge.copy(fontFamily = boldFamily),
            labelMedium = TYPOGRAPHY.labelMedium.copy(fontFamily = boldFamily),
            labelSmall = TYPOGRAPHY.labelSmall.copy(fontFamily = boldFamily)
        )
    }
}

@Composable
fun getAppFonts(): AppFonts {
    val isRtl = isRtlLocale()

    // For RTL locales (Persian, Arabic, Hebrew, etc.), use Vazirmatn; otherwise fall back to Google Sans Flex
    if (isRtl) {
        val vazirRegular = Font(Res.font.vazirmatn_regular, FontWeight.Normal)
        val vazirMedium  = Font(Res.font.vazirmatn_medium,  FontWeight.Bold)
        val vazirFamily  = remember(vazirRegular, vazirMedium) { FontFamily(vazirRegular, vazirMedium) }
        return AppFonts(
            topBarTitle = vazirFamily,
            unselectedTopicTitle = vazirFamily,
            annotatedString = vazirFamily
        )
    }

    val robotoFlexTopBarFont = Font(
        Res.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(900),
            FontVariation.width(112.5f),
            FontVariation.Setting("ROND", 35f)
        )
    )
    val robotoFlexTopBar = remember(robotoFlexTopBarFont) { FontFamily(robotoFlexTopBarFont) }

    val unselectedTopicTitleFont = Font(
        Res.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600),
            FontVariation.width(100f),
            FontVariation.Setting("ROND", 35f)
        )
    )
    val unselectedTopicTitle =
        remember(unselectedTopicTitleFont) { FontFamily(unselectedTopicTitleFont) }

    val annotatedStringNormalFont = Font(
        Res.font.google_sans_flex,
        FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    )
    val annotatedStringBoldFont = Font(
        Res.font.google_sans_flex,
        FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600),
            FontVariation.Setting("ROND", 100f)
        )
    ) // Used for <b> tags

    val annotatedStringFontFamily = remember(annotatedStringNormalFont, annotatedStringBoldFont) {
        FontFamily(annotatedStringNormalFont, annotatedStringBoldFont)
    }

    return AppFonts(
        topBarTitle = robotoFlexTopBar,
        unselectedTopicTitle = unselectedTopicTitle,
        annotatedString = annotatedStringFontFamily
    )
}

val LocalAppFonts = staticCompositionLocalOf<AppFonts> { error("AppFonts not provided") }
