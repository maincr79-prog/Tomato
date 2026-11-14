/*
 * Copyright (c) 2026 Mohammad (Focus Glass modifications)
 *
 * This file is part of Focus Glass, a modified version of Tomato, licensed under GPL-3.0.
 */

package org.nsh07.pomodoro.utils

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StudyTest {
    private val today = LocalDate.of(2026, 11, 14)
    private fun d(daysAgo: Long) = today.minusDays(daysAgo)

    // ---- streak ----
    @Test
    fun `streak is zero with no focus days`() {
        assertEquals(0, focusStreak(emptySet(), today))
    }

    @Test
    fun `streak counts today and consecutive days before it`() {
        assertEquals(3, focusStreak(setOf(d(0), d(1), d(2)), today))
    }

    @Test
    fun `empty today does not break yesterday's streak`() {
        assertEquals(2, focusStreak(setOf(d(1), d(2)), today))
    }

    @Test
    fun `gap ends the streak`() {
        assertEquals(1, focusStreak(setOf(d(0), d(2), d(3)), today))
    }

    @Test
    fun `streak is zero when last focus was two days ago`() {
        assertEquals(0, focusStreak(setOf(d(2), d(3)), today))
    }

    // ---- Konkur percent (3 correct cancel 1 wrong) ----
    @Test
    fun `konkur percent all correct is 100`() {
        assertEquals(100f, konkurPercent(correct = 10, wrong = 0, blank = 0))
    }

    @Test
    fun `konkur percent with negative marking`() {
        // (3*6 - 3) / (3*10) * 100 = 50
        assertEquals(50f, konkurPercent(correct = 6, wrong = 3, blank = 1))
    }

    @Test
    fun `konkur percent can be negative`() {
        // (0 - 3) / (3*3) * 100 = -33.33
        assertEquals(-33.33f, konkurPercent(correct = 0, wrong = 3, blank = 0)!!, 0.01f)
    }

    @Test
    fun `konkur percent is null when nothing answered`() {
        assertNull(konkurPercent(0, 0, 0))
    }

    // ---- days until Konkur ----
    @Test
    fun `days until exam`() {
        assertEquals(10, daysUntil(today.plusDays(10), today))
    }

    @Test
    fun `days until is zero on exam day and past`() {
        assertEquals(0, daysUntil(today, today))
        assertEquals(0, daysUntil(today.minusDays(5), today))
    }
}
