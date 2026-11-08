/*
 * Copyright (c) 2026 Nishant Mishra
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

import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import org.nsh07.pomodoro.data.Topic.Companion.defaultTopic

/**
 * Writes [defaultTopic] into a freshly created database
 */
object SeedDefaultTopicCallback : RoomDatabase.Callback() {
    override suspend fun onCreate(connection: SQLiteConnection) {
        super.onCreate(connection)
        // Default topic
        connection.execSQL(
            """
            INSERT OR IGNORE INTO `topic` 
                (`id`, `name`, `color`, `shape`, `focusTime`, `shortBreakTime`, `longBreakTime`, `sessionLength`, `autostartNextSession`, `dndEnabled`)
            VALUES (
                ${defaultTopic.id}, 
                '${defaultTopic.name}', 
                ${defaultTopic.color.value.toLong()},
                '${defaultTopic.shape.name}',
                ${defaultTopic.focusTime},
                ${defaultTopic.shortBreakTime},
                ${defaultTopic.longBreakTime},
                ${defaultTopic.sessionLength},
                ${if (defaultTopic.autostartNextSession) 1 else 0},
                ${if (defaultTopic.dndEnabled) 1 else 0}
            )
            """.trimIndent()
        )

        // Konkur & Study Subjects
        // Colors use correct Compose Color.value.toLong() format:
        // (argbInt.toLong() and 0xFFFFFFFFL) shl 32 converted to signed Long
        val subjects = listOf(
            Triple("زیست‌شناسی", -50472737675673600L,  "CLOVER_4_LEAF"), // 0xFF4CAF50
            Triple("ریاضیات",    -62602949385256960L,  "TRIANGLE"),      // 0xFF2196F3
            Triple("فیزیک",      -28103860803338240L,  "BURST"),         // 0xFF9C27B0
            Triple("شیمی",       -114349209288704L,    "GEM"),           // 0xFFFF9800
            Triple("عمومی و متفرقه", -71892083178209280L, "COOKIE_7_SIDED") // 0xFF009688
        )

        subjects.forEachIndexed { index, (name, colorLong, shapeName) ->
            val id = index + 2L
            connection.execSQL(
                """
                INSERT OR IGNORE INTO `topic`
                    (`id`, `name`, `color`, `shape`, `focusTime`, `shortBreakTime`, `longBreakTime`, `sessionLength`, `autostartNextSession`, `dndEnabled`)
                VALUES (
                    $id,
                    '$name',
                    $colorLong,
                    '$shapeName',
                    ${45 * 60 * 1000L},
                    ${10 * 60 * 1000L},
                    ${15 * 60 * 1000L},
                    4,
                    0,
                    0
                )
                """.trimIndent()
            )
        }
    }
}
