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

package org.nsh07.pomodoro.ui.timerScreen.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.nsh07.pomodoro.data.Stat
import org.nsh07.pomodoro.data.StatRepository
import org.nsh07.pomodoro.data.StateRepository
import org.nsh07.pomodoro.data.TestLogRepository
import org.nsh07.pomodoro.data.TopicRepository
import org.nsh07.pomodoro.service.TimerHelper
import org.nsh07.pomodoro.ui.Screen
import org.nsh07.pomodoro.utils.focusStreak
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class TimerViewModel(
    private val timerHelper: TimerHelper,
    private val stateRepository: StateRepository,
    private val statRepository: StatRepository,
    private val topicRepository: TopicRepository,
    private val testLogRepository: TestLogRepository
) : ViewModel() {
    val rootBackstack = mutableStateListOf<Screen>(Screen.Timer)

    private val _time: MutableStateFlow<Long> = stateRepository.time
    val timerState: StateFlow<TimerState> = stateRepository.timerState.asStateFlow()

    val currentTopic = stateRepository.currentTopic

    val progress = _time.combine(stateRepository.timerState) { remainingTime, uiState ->
        (uiState.totalTime.toFloat() - remainingTime) / uiState.totalTime
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    /** Today's focus time, daily goal, tests and streak — shown in the glass "Today" card. */
    val today: StateFlow<TodaySummary> = combine(
        statRepository.getTodayStat(),
        statRepository.getLastNDaysStats(366),
        testLogRepository.getTodayTotalTests(),
        stateRepository.settingsState
    ) { todayStat, days, tests, settings ->
        TodaySummary(
            focusMillis = todayStat?.totalFocusTime() ?: 0L,
            goalMillis = settings.focusGoal,
            tests = tests,
            streak = focusStreak(days.filter { it.totalFocusTime() > 0 }.map { it.date }.toSet())
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodaySummary())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val topicIds = topicRepository.getTopicIds()
            var lastDate = statRepository.getLastDate()
            val today = LocalDate.now()

            // Fills dates between today and lastDate with 0s to ensure continuous history
            if (lastDate != null) {
                while (ChronoUnit.DAYS.between(lastDate, today) > 0) {
                    lastDate = lastDate?.plusDays(1)
                    topicIds.forEach { topicId ->
                        statRepository.insertStat(Stat(lastDate!!, topicId, 0, 0, 0, 0, 0))
                    }
                }
            } else {
                topicIds.forEach { topicId ->
                    statRepository.insertStat(Stat(today, topicId, 0, 0, 0, 0, 0))
                }
            }

            delay(1500.milliseconds)

            stateRepository.timerState.update { currentState ->
                currentState.copy(showBrandTitle = false)
            }
        }
    }

    fun onAction(action: TimerAction) {
        when (action) {
            is TimerAction.SetInfiniteFocus -> {
                stateRepository.timerState.update {
                    it.copy(
                        infiniteFocus = action.value
                    )
                }
                onAction(TimerAction.ResetTimer)
            }

            is TimerAction.SetTopic -> {
                if (!timerState.value.sessionActive) {
                    viewModelScope.launch {
                        stateRepository.setTopic(action.topic)
                        onAction(TimerAction.ResetTimer)
                    }
                }
            }

            else -> timerHelper.onAction(action)
        }
    }

    fun logTests(testCount: Int, note: String) {
        if (testCount > 0) {
            viewModelScope.launch(Dispatchers.IO) {
                testLogRepository.logTests(
                    topicId = currentTopic.value.id,
                    testCount = testCount,
                    durationMillis = currentTopic.value.focusTime,
                    note = note
                )
            }
        }
    }
}
/** Numbers for the "Today" card on the timer screen. */
data class TodaySummary(
    val focusMillis: Long = 0L,
    val goalMillis: Long = 0L,
    val tests: Int = 0,
    val streak: Int = 0
) {
    /** 0..1 progress towards the daily goal (0 when no goal is set). */
    val goalProgress: Float
        get() = if (goalMillis <= 0) 0f else (focusMillis.toFloat() / goalMillis).coerceIn(0f, 1f)
}
