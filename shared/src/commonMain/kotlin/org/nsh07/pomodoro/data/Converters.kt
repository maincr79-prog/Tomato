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

package org.nsh07.pomodoro.data

import androidx.compose.ui.graphics.Color
import androidx.room3.ColumnTypeConverter
import java.time.LocalDate

object LocalDateConverter {
    @ColumnTypeConverter
    fun localDateToString(localDate: LocalDate?): String? {
        return localDate?.toString()
    }

    @ColumnTypeConverter
    fun stringToLocalDate(date: String?): LocalDate? {
        return if (date != null) LocalDate.parse(date) else null
    }
}

object ComposeColorConverter {
    @ColumnTypeConverter
    fun fromColor(color: Color): Long {
        return color.value.toLong()
    }

    /**
     * Crash-proof decoding. Older builds stored raw ARGB ints (e.g. 0xFF2196F3) instead of
     * Compose's packed ULong. Compose reads the low 6 bits as a color-space id, so a value
     * like 0xFF2196F3 → id 51 (doesn't exist) → crash whenever that topic is shown.
     */
    @ColumnTypeConverter
    fun toColor(value: Long): Color {
        // Legacy: whole value fits in 32 bits → it's a plain ARGB int.
        if (value ushr 32 == 0L) return Color(value.toInt())
        val packed = value.toULong()
        // Packed sRGB (color-space id 0) is the only format the app ever writes.
        if ((packed and 0x3FUL) == 0UL) return Color(packed)
        // Anything else is corrupt — fall back to "no seed" instead of crashing.
        return runCatching { Color(packed).also { it.colorSpace } }.getOrDefault(Color.White)
    }
}