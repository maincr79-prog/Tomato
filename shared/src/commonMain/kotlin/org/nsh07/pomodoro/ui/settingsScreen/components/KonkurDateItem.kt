/*
 * Copyright (c) 2026 Mohammad (Focus Glass modifications)
 *
 * This file is part of Focus Glass, a modified version of Tomato, licensed under GPL-3.0.
 */

package org.nsh07.pomodoro.ui.settingsScreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.nsh07.pomodoro.ui.theme.CustomColors.listItemColors
import org.nsh07.pomodoro.utils.daysUntil
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.cancel
import tomato.shared.generated.resources.clear
import tomato.shared.generated.resources.konkur_countdown
import tomato.shared.generated.resources.konkur_date
import tomato.shared.generated.resources.konkur_date_unset
import tomato.shared.generated.resources.ok
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private const val DAY_MS = 24L * 60 * 60 * 1000

/** Settings row to pick (or clear) the Konkur exam date. [epochDay] 0 = not set. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KonkurDateItem(epochDay: Long, onSave: (Long) -> Unit, modifier: Modifier = Modifier) {
    var open by remember { mutableStateOf(false) }
    val date = epochDay.takeIf { it > 0 }?.let { LocalDate.ofEpochDay(it) }

    ListItem(
        leadingContent = {
            Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) { Text("⏳") }
        },
        headlineContent = { Text(stringResource(Res.string.konkur_date)) },
        supportingContent = {
            Text(
                if (date == null) stringResource(Res.string.konkur_date_unset)
                else date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) +
                        "  ·  " + stringResource(Res.string.konkur_countdown, daysUntil(date))
            )
        },
        colors = listItemColors,
        modifier = modifier
            .clip(shapes.large)
            .clickable { open = true }
    )

    if (open) {
        val today = LocalDate.now().toEpochDay()
        val opaque = colorScheme.surfaceContainerHigh.copy(alpha = 1f)
        val state = rememberDatePickerState(
            initialSelectedDateMillis = (date?.toEpochDay() ?: (today + 180)) * DAY_MS
        )
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { onSave(it / DAY_MS) }
                    open = false
                }) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                Row {
                    if (date != null) TextButton(onClick = { onSave(0); open = false }) {
                        Text(stringResource(Res.string.clear), color = colorScheme.error)
                    }
                    TextButton(onClick = { open = false }) { Text(stringResource(Res.string.cancel)) }
                }
            },
            shape = RoundedCornerShape(28.dp),
            // Glass theme makes containers translucent; a dialog must be opaque
            colors = DatePickerDefaults.colors(containerColor = opaque)
        ) {
            DatePicker(
                state = state,
                colors = DatePickerDefaults.colors(containerColor = opaque)
            )
        }
    }
}
