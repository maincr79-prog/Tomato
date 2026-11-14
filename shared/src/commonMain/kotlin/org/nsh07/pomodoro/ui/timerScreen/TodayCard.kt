package org.nsh07.pomodoro.ui.timerScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.nsh07.pomodoro.ui.theme.glass
import org.nsh07.pomodoro.ui.timerScreen.viewModel.TodaySummary
import org.nsh07.pomodoro.utils.millisecondsToHoursMinutes
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.focus_streak
import tomato.shared.generated.resources.goal_reached
import tomato.shared.generated.resources.hours_and_minutes_format
import tomato.shared.generated.resources.konkur_countdown
import tomato.shared.generated.resources.konkur_today
import tomato.shared.generated.resources.set_goal_hint
import tomato.shared.generated.resources.tests_today
import tomato.shared.generated.resources.today_focus

/**
 * Small glass card under the timer: today's focus time, a ring towards the daily goal,
 * the streak (days in a row) and how many tests were logged today.
 */
@Composable
fun TodayCard(today: TodaySummary, modifier: Modifier = Modifier) {
    val hmf = stringResource(Res.string.hours_and_minutes_format)
    val ring by animateFloatAsState(
        targetValue = today.goalProgress,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 120f)
    )
    val primary = colorScheme.primary
    val track = colorScheme.primary.copy(alpha = 0.22f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .glass(RoundedCornerShape(28.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        // Goal ring
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp)) {
            Canvas(Modifier.size(56.dp)) {
                val w = 7.dp.toPx()
                val arcSize = Size(size.width - w, size.height - w)
                val topLeft = Offset(w / 2, w / 2)
                drawArc(track, 0f, 360f, false, topLeft, arcSize, style = Stroke(w))
                drawArc(primary, -90f, 360f * ring, false, topLeft, arcSize, style = Stroke(w, cap = StrokeCap.Round))
            }
            Text(
                when {
                    today.goalMillis <= 0 -> "🎯"
                    today.goalProgress >= 1f -> "✓"
                    else -> "${(today.goalProgress * 100).toInt()}%"
                },
                style = typography.labelMedium,
                color = colorScheme.onSurface
            )
        }

        Column(Modifier.weight(1f)) {
            Text(
                stringResource(Res.string.today_focus),
                style = typography.labelLarge,
                color = colorScheme.onSurfaceVariant
            )
            Text(
                if (today.goalMillis > 0)
                    millisecondsToHoursMinutes(today.focusMillis, hmf) + "  /  " +
                            millisecondsToHoursMinutes(today.goalMillis, hmf)
                else millisecondsToHoursMinutes(today.focusMillis, hmf),
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
            Text(
                stringResource(Res.string.focus_streak, today.streak) +
                        "   " + stringResource(Res.string.tests_today, today.tests),
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
            today.daysToKonkur?.let { days ->
                Text(
                    if (days > 0) stringResource(Res.string.konkur_countdown, days)
                    else stringResource(Res.string.konkur_today),
                    style = typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.tertiary
                )
            }
            if (today.goalMillis <= 0 || today.goalProgress >= 1f) Text(
                if (today.goalMillis <= 0) stringResource(Res.string.set_goal_hint)
                else stringResource(Res.string.goal_reached),
                style = typography.bodySmall,
                color = colorScheme.primary
            )
        }
    }
}
