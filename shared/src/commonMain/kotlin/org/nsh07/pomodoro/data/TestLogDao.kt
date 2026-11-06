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

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TestLogDao {
    @Insert
    suspend fun insert(testLog: TestLog): Long

    @Query("SELECT * FROM test_log ORDER BY id DESC")
    fun getAllLogs(): Flow<List<TestLog>>

    @Query("SELECT * FROM test_log WHERE date = :date ORDER BY id DESC")
    fun getLogsForDate(date: LocalDate): Flow<List<TestLog>>

    @Query("SELECT COALESCE(SUM(testCount), 0) FROM test_log WHERE date = :date")
    fun getTotalTestsForDate(date: LocalDate): Flow<Int>

    @Query("SELECT COALESCE(SUM(testCount), 0) FROM test_log")
    fun getTotalTestsAllTime(): Flow<Int>

    @Query("SELECT COALESCE(SUM(testCount), 0) FROM test_log WHERE date >= :startDate")
    fun getTestsSince(startDate: LocalDate): Flow<Int>

    @Query("DELETE FROM test_log")
    suspend fun deleteAll()
}
