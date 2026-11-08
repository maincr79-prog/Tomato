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

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import org.nsh07.pomodoro.data.Topic.Companion.defaultTopic

val MIGRATION_2_3 = object : Migration(2, 3) {
    // Color.White.value.toLong() = -4294967296L
    private val colorWhiteLong = -4294967296L
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `topic` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL COLLATE NOCASE,
                `color` INTEGER NOT NULL,
                `shape` TEXT NOT NULL,
                `focusTime` INTEGER NOT NULL,
                `shortBreakTime` INTEGER NOT NULL,
                `longBreakTime` INTEGER NOT NULL,
                `sessionLength` INTEGER NOT NULL,
                `autostartNextSession` INTEGER NOT NULL,
                `dndEnabled` INTEGER NOT NULL
            )
            """.trimIndent()
        )

        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_topic_name` ON `topic` (`name`)")

        // Seed default topic with current global settings or hardcoded defaults if preferences don't exist
        connection.execSQL(
            """
            INSERT OR IGNORE INTO `topic`
                (`id`, `name`, `color`, `shape`, `focusTime`, `shortBreakTime`, `longBreakTime`, `sessionLength`, `autostartNextSession`, `dndEnabled`)
            VALUES (
                ${defaultTopic.id},
                '${defaultTopic.name}',
                $colorWhiteLong,
                '${defaultTopic.shape.name}',
                COALESCE((SELECT value FROM int_preference WHERE key = 'focus_time'), 1500000),
                COALESCE((SELECT value FROM int_preference WHERE key = 'short_break_time'), 300000),
                COALESCE((SELECT value FROM int_preference WHERE key = 'long_break_time'), 900000),
                COALESCE((SELECT value FROM int_preference WHERE key = 'session_length'), 4),
                COALESCE((SELECT value FROM boolean_preference WHERE key = 'autostart_next_session'), 0),
                COALESCE((SELECT value FROM boolean_preference WHERE key = 'dnd_enabled'), 0)
            )
            """.trimIndent()
        )

        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `new_stat` (
                `date` TEXT NOT NULL,
                `topicId` INTEGER NOT NULL,
                `focusTimeQ1` INTEGER NOT NULL, 
                `focusTimeQ2` INTEGER NOT NULL, 
                `focusTimeQ3` INTEGER NOT NULL, 
                `focusTimeQ4` INTEGER NOT NULL, 
                `breakTime` INTEGER NOT NULL, 
                PRIMARY KEY(`date`, `topicId`), 
                FOREIGN KEY(`topicId`) REFERENCES `topic`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )

        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_stat_topicId` ON `new_stat` (`topicId`)")

        // Copy existing data into new_stat, mapping to 'default' topic
        connection.execSQL(
            """
            INSERT INTO `new_stat` (`date`, `topicId`, `focusTimeQ1`, `focusTimeQ2`, `focusTimeQ3`, `focusTimeQ4`, `breakTime`)
            SELECT `date`, ${defaultTopic.id}, `focusTimeQ1`, `focusTimeQ2`, `focusTimeQ3`, `focusTimeQ4`, `breakTime` FROM `stat`
            """.trimIndent()
        )

        connection.execSQL("DROP TABLE `stat`")
        connection.execSQL("ALTER TABLE `new_stat` RENAME TO `stat`")

        // Clean up migrated settings from global preference tables
        connection.execSQL("DELETE FROM int_preference WHERE key IN ('focus_time', 'short_break_time', 'long_break_time', 'session_length')")
        connection.execSQL("DELETE FROM boolean_preference WHERE key IN ('autostart_next_session', 'dnd_enabled')")
    }
}

// Correct color.value.toLong() representations for ARGB colors:
// These are the values that Color(ARGB).value.toLong() produces in Compose Multiplatform
// Computed as: (argbInt.toLong() and 0xFFFFFFFFL) shl 32 converted to signed Long
private object KonkurColors {
    const val GREEN_BIOLOGY  = -50472737675673600L  // 0xFF4CAF50
    const val BLUE_MATH      = -62602949385256960L  // 0xFF2196F3
    const val PURPLE_PHYSICS = -28103860803338240L  // 0xFF9C27B0
    const val ORANGE_CHEM    = -114349209288704L    // 0xFFFF9800
    const val TEAL_GENERAL   = -71892083178209280L  // 0xFF009688
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `test_log` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `date` TEXT NOT NULL,
                `topicId` INTEGER NOT NULL,
                `testCount` INTEGER NOT NULL,
                `durationMillis` INTEGER NOT NULL,
                `note` TEXT NOT NULL,
                `timestamp` INTEGER NOT NULL
            )
            """.trimIndent()
        )

        val subjects = listOf(
            Triple("زیست‌شناسی", KonkurColors.GREEN_BIOLOGY,  "CLOVER_4_LEAF"),
            Triple("ریاضیات",    KonkurColors.BLUE_MATH,      "TRIANGLE"),
            Triple("فیزیک",      KonkurColors.PURPLE_PHYSICS, "BURST"),
            Triple("شیمی",       KonkurColors.ORANGE_CHEM,    "GEM"),
            Triple("عمومی و متفرقه", KonkurColors.TEAL_GENERAL, "COOKIE_7_SIDED")
        )

        subjects.forEach { (name, colorLong, shapeName) ->
            connection.execSQL(
                """
                INSERT OR IGNORE INTO `topic`
                    (`name`, `color`, `shape`, `focusTime`, `shortBreakTime`, `longBreakTime`, `sessionLength`, `autostartNextSession`, `dndEnabled`)
                VALUES (
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

/** Fixes incorrect color values stored by MIGRATION_3_4 and SeedDefaultTopicCallback (v4). */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override suspend fun migrate(connection: SQLiteConnection) {
        // Update color values for konkur subjects that were stored with wrong (Int-signed) values
        // The incorrect values were the raw negative Int literals instead of proper Compose ULong values
        val colorFixes = mapOf(
            "زیست‌شناسی" to KonkurColors.GREEN_BIOLOGY,
            "ریاضیات"    to KonkurColors.BLUE_MATH,
            "فیزیک"      to KonkurColors.PURPLE_PHYSICS,
            "شیمی"       to KonkurColors.ORANGE_CHEM,
            "عمومی و متفرقه" to KonkurColors.TEAL_GENERAL
        )
        colorFixes.forEach { (name, correctColor) ->
            connection.execSQL(
                "UPDATE `topic` SET `color` = $correctColor WHERE `name` = '$name'"
            )
        }
    }
}

