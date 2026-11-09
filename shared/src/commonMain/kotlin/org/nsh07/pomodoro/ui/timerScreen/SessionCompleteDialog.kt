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

package org.nsh07.pomodoro.ui.timerScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.nsh07.pomodoro.data.Topic
import org.nsh07.pomodoro.ui.performConfirm
import org.nsh07.pomodoro.ui.performSegmentTick
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.alarm
import tomato.shared.generated.resources.completed
import tomato.shared.generated.resources.stop_alarm
import tomato.shared.generated.resources.session_done_title
import tomato.shared.generated.resources.tests_solved_label
import tomato.shared.generated.resources.tests_count_fmt
import tomato.shared.generated.resources.session_note_hint
import tomato.shared.generated.resources.skip_and_break
import tomato.shared.generated.resources.save_tests_and_break

@Composable
fun SessionCompleteDialog(
    currentTopic: Topic,
    onSaveTests: (testCount: Int, note: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var testCount by remember { mutableIntStateOf(0) }
    var note by remember { mutableStateOf("") }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.scrim.copy(alpha = 0.6f))
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerHigh),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(0.92f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // Header badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(colorScheme.primaryContainer)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.alarm),
                        contentDescription = stringResource(Res.string.completed),
                        tint = colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = stringResource(Res.string.session_done_title),
                    style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    color = colorScheme.onSurface
                )

                Spacer(Modifier.height(6.dp))

                // Subject Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(currentTopic.color.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(currentTopic.color)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = currentTopic.name,
                        style = typography.labelLarge,
                        color = colorScheme.onSurface
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Test Counter section
                Text(
                    text = stringResource(Res.string.tests_solved_label),
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilledTonalButton(
                        onClick = {
                            if (testCount >= 5) {
                                testCount -= 5
                                haptic.performSegmentTick()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(48.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text("-5", style = typography.titleMedium)
                    }

                    Spacer(Modifier.width(6.dp))

                    FilledTonalButton(
                        onClick = {
                            if (testCount >= 1) {
                                testCount -= 1
                                haptic.performSegmentTick()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(48.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text("-1", style = typography.titleMedium)
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colorScheme.surfaceContainerHighest)
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.tests_count_fmt, testCount),
                            style = typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = colorScheme.primary
                            )
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            testCount += 1
                            haptic.performSegmentTick()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(48.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text("+1", style = typography.titleMedium)
                    }

                    Spacer(Modifier.width(6.dp))

                    FilledTonalButton(
                        onClick = {
                            testCount += 5
                            haptic.performSegmentTick()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(48.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text("+5", style = typography.titleMedium)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Optional note field
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(stringResource(Res.string.session_note_hint), fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            haptic.performConfirm()
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = stringResource(Res.string.skip_and_break),
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            haptic.performConfirm()
                            onSaveTests(testCount, note)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (testCount > 0) stringResource(Res.string.save_tests_and_break) else stringResource(Res.string.stop_alarm),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
