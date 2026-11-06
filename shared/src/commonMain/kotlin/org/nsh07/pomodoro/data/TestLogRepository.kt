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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate

interface TestLogRepository {
    suspend fun logTests(
        topicId: Long,
        testCount: Int,
        durationMillis: Long = 0,
        note: String = "",
        date: LocalDate = LocalDate.now()
    ): Long

    fun getTodayTotalTests(): Flow<Int>
    fun getAllTimeTotalTests(): Flow<Int>
    fun getLogsForDate(date: LocalDate): Flow<List<TestLog>>
    suspend fun deleteAll()
}

class AppTestLogRepository(
    private val testLogDao: TestLogDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : TestLogRepository {

    override suspend fun logTests(
        topicId: Long,
        testCount: Int,
        durationMillis: Long,
        note: String,
        date: LocalDate
    ): Long = withContext(dispatcher) {
        val entry = TestLog(
            date = date,
            topicId = topicId,
            testCount = testCount,
            durationMillis = durationMillis,
            note = note,
            timestamp = System.currentTimeMillis()
        )
        testLogDao.insert(entry)
    }

    override fun getTodayTotalTests(): Flow<Int> =
        testLogDao.getTotalTestsForDate(LocalDate.now())

    override fun getAllTimeTotalTests(): Flow<Int> =
        testLogDao.getTotalTestsAllTime()

    override fun getLogsForDate(date: LocalDate): Flow<List<TestLog>> =
        testLogDao.getLogsForDate(date)

    override suspend fun deleteAll() = withContext(dispatcher) {
        testLogDao.deleteAll()
    }
}
