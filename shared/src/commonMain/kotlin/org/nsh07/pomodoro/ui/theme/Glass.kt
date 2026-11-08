package org.nsh07.pomodoro.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Calm blue used as the default seed (instead of the wallpaper colour). */
val GlassBlue = Color(0xFF4A7FD4)

/**
 * "Glass" look: the page background becomes transparent so the soft gradient drawn by
 * [GlassBackground] shows through, and cards / bars / sheets become semi-transparent.
 * No real-time blur is used on purpose — it is expensive and would make the app slower.
 */
fun ColorScheme.glassify(): ColorScheme = copy(
    background = Color.Transparent,
    surface = surface.copy(alpha = 0.80f),
    surfaceContainerLowest = surfaceContainerLowest.copy(alpha = 0.55f),
    surfaceContainerLow = surfaceContainerLow.copy(alpha = 0.72f),
    surfaceContainer = surfaceContainer.copy(alpha = 0.78f),
    surfaceContainerHigh = surfaceContainerHigh.copy(alpha = 0.88f),
    surfaceContainerHighest = surfaceContainerHighest.copy(alpha = 0.88f),
    secondaryContainer = secondaryContainer.copy(alpha = 0.92f),
)

/**
 * Turns any element into an iOS-style glass pane:
 * soft shadow + frosted milky fill + thin "rim light" that is bright at the top.
 * No blur → cheap enough to use everywhere.
 *
 * @param tint optional colour wash on top of the frost (e.g. the accent for a "prominent" button)
 */
@Composable
fun Modifier.glass(
    shape: Shape = RoundedCornerShape(28.dp),
    elevation: Dp = 10.dp,
    tint: Color = Color.Transparent
): Modifier {
    val dark = LocalDarkTheme.current
    val fill = if (dark) Color.White.copy(alpha = 0.09f) else Color.White.copy(alpha = 0.42f)
    val rim = if (dark) Brush.verticalGradient(
        listOf(Color.White.copy(alpha = 0.32f), Color.White.copy(alpha = 0.05f))
    ) else Brush.verticalGradient(
        listOf(Color.White, Color(0xFFB8CBEA).copy(alpha = 0.55f))
    )
    val shadowColor = if (dark) Color.Black.copy(alpha = 0.5f) else Color(0xFF2A4A80).copy(alpha = 0.16f)

    return this
        .dropShadow(shape, Shadow(radius = elevation * 2, color = shadowColor, offset = DpOffset(0.dp, elevation / 3)))
        .clip(shape)
        .background(fill)
        .background(tint)
        .border(1.dp, rim, shape)
}

/**
 * Soft "mesh" background: a calm blue gradient with three glowing colour blobs (blue, aqua and
 * the topic's second colour). Redrawn only when size or colours change → almost free.
 */
@Composable
fun GlassBackground(dark: Boolean, content: @Composable () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val top = if (dark) Color(0xFF0A1322) else Color(0xFFEFF5FF)
    val bottom = if (dark) Color(0xFF0F1A30) else Color(0xFFDCE8FC)
    val glow = if (dark) 0.30f else 0.26f
    val aqua = if (dark) Color(0xFF3FA8C9) else Color(0xFF7CC8E8)

    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(Brush.verticalGradient(listOf(top, bottom)))
                blob(primary.copy(alpha = glow), Offset(size.width * 0.10f, size.height * 0.10f), size.width * 0.95f)
                blob(aqua.copy(alpha = glow * 0.9f), Offset(size.width, size.height * 0.45f), size.width * 0.75f)
                blob(tertiary.copy(alpha = glow * 0.8f), Offset(size.width * 0.15f, size.height * 0.95f), size.width * 0.95f)
            }
    ) { content() }
}

private fun DrawScope.blob(color: Color, center: Offset, radius: Float) {
    drawCircle(
        Brush.radialGradient(listOf(color, Color.Transparent), center = center, radius = radius),
        radius = radius,
        center = center
    )
}
