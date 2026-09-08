package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlexAmber
import com.example.ui.theme.PlexAmberGlow
import com.example.ui.theme.PlexBorder
import com.example.ui.theme.PlexTextMuted
import com.example.ui.theme.PlexTextSecondary
import kotlin.math.abs
import kotlin.math.sin

/**
 * Iconic Plexamp Loudness Waveform Scrubber.
 * Renders an interactive amplitude bar profile across the playback duration.
 */
@Composable
fun PlexampWaveformScrubber(
    positionMs: Long,
    durationMs: Long,
    trackSeed: String,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier,
    barCount: Int = 48,
    activeColor: Color = PlexAmber,
    inactiveColor: Color = PlexBorder,
    showRemainingTime: Boolean = true
) {
    val progress = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    var isDragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableFloatStateOf(0f) }

    val currentFraction = if (isDragging) dragFraction else progress

    // Deterministic realistic loudness profile based on track seed
    val amplitudes = remember(trackSeed, barCount) {
        val seed = trackSeed.hashCode().toLong()
        val random = java.util.Random(seed)
        List(barCount) { i ->
            val normI = i.toFloat() / barCount
            // Musical envelope: quiet intro, energetic chorus/bridge, fade out
            val envelope = (sin(normI * Math.PI).toFloat()).coerceIn(0.2f, 1.0f)
            val variation = 0.35f + 0.65f * random.nextFloat()
            (envelope * variation).coerceIn(0.15f, 0.95f)
        }
    }

    Column(modifier = modifier.fillMaxWidth().testTag("plexamp_waveform_scrubber")) {
        // Waveform Bar Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .pointerInput(durationMs) {
                    detectTapGestures { offset ->
                        if (size.width > 0 && durationMs > 0) {
                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                            onSeekTo((fraction * durationMs).toLong())
                        }
                    }
                }
                .pointerInput(durationMs) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            dragFraction = (offset.x / size.width).coerceIn(0f, 1f)
                        },
                        onDragEnd = {
                            isDragging = false
                            if (durationMs > 0) {
                                onSeekTo((dragFraction * durationMs).toLong())
                            }
                        },
                        onDragCancel = {
                            isDragging = false
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            dragFraction = (change.position.x / size.width).coerceIn(0f, 1f)
                        }
                    )
                }
        ) {
            val totalWidth = size.width
            val totalHeight = size.height
            val barSpacing = 2.5f.dp.toPx()
            val availableBarWidth = (totalWidth - (barSpacing * (barCount - 1))) / barCount
            val barWidth = availableBarWidth.coerceAtLeast(1.5f.dp.toPx())

            val activeIndex = (currentFraction * barCount).toInt().coerceIn(0, barCount - 1)

            for (i in 0 until barCount) {
                val x = i * (barWidth + barSpacing)
                val amp = amplitudes[i]
                val barHeight = (totalHeight * amp).coerceAtLeast(3.dp.toPx())
                val y = (totalHeight - barHeight) / 2f

                val isPlayed = i <= activeIndex
                val isPlayhead = i == activeIndex

                val barColor = when {
                    isPlayhead -> PlexAmberGlow
                    isPlayed -> activeColor
                    else -> inactiveColor
                }

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Time indicators
        val currentMs = if (isDragging) (dragFraction * durationMs).toLong() else positionMs
        val remainingMs = (durationMs - currentMs).coerceAtLeast(0L)

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatMs(currentMs),
                color = if (isDragging) PlexAmber else PlexTextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = if (showRemainingTime && durationMs > 0) "-${formatMs(remainingMs)}" else formatMs(durationMs),
                color = PlexTextMuted,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
