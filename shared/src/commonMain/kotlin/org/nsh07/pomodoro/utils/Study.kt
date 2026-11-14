/*
 * Copyright (c) 2026 Mohammad (Focus Glass modifications)
 *
 * This file is part of Focus Glass, a modified version of Tomato, licensed under GPL-3.0.
 */

package org.nsh07.pomodoro.utils

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Days in a row with focus, ending today. Today only counts once it has focus, so an
 * unfinished today does not break yesterday's streak.
 */
fun focusStreak(focusDays: Set<LocalDate>, today: LocalDate = LocalDate.now()): Int {
    var day = if (today in focusDays) today else today.minusDays(1)
    var streak = 0
    while (day in focusDays) {
        streak++
        day = day.minusDays(1)
    }
    return streak
}

/**
 * Konkur-style percent with negative marking (3 wrong answers cancel 1 correct):
 * (3·correct − wrong) / (3·total) × 100. Returns null when no questions were recorded.
 */
fun konkurPercent(correct: Int, wrong: Int, blank: Int): Float? {
    val total = correct + wrong + blank
    if (total <= 0) return null
    return (3f * correct - wrong) / (3f * total) * 100f
}

/** Whole days from [today] until [target]; 0 on the day itself or after it. */
fun daysUntil(target: LocalDate, today: LocalDate = LocalDate.now()): Int =
    ChronoUnit.DAYS.between(today, target).toInt().coerceAtLeast(0)
