package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.TrackEntity
import com.example.ui.theme.PlexAmber
import com.example.ui.theme.PlexAmberGlow
import com.example.ui.theme.PlexBackground
import com.example.ui.theme.PlexBorder
import com.example.ui.theme.PlexCard
import com.example.ui.theme.PlexCardElevated
import com.example.ui.theme.PlexSurface
import com.example.ui.theme.PlexTextMuted
import com.example.ui.theme.PlexTextPrimary
import com.example.ui.theme.PlexTextSecondary

/**
 * Plexamp EV Car Cockpit Mode.
 * Engineered specifically for vehicle displays (e.g. Chevrolet Equinox EV 17.7-inch screen)
 * with zero-distraction high contrast, massive 88dp interactive touch targets,
 * live audio pulse visualization, and hands-free voice trigger.
 */
@Composable
fun DriverModeView(
    currentTrack: TrackEntity?,
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleFavorite: (TrackEntity) -> Unit,
    onStartVoice: () -> Unit,
    onExitDriverMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audioPulse")
    val pulseHeight by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseHeight"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PlexBackground)
            .padding(28.dp)
            .testTag("driver_mode_view")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(PlexCard)
                            .border(1.dp, PlexBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = PlexAmber,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "STREAMWAVE CAR MODE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.4.sp,
                                color = PlexAmber
                            )
                        )
                        Text(
                            text = "Distraction-Free EV Interface",
                            style = MaterialTheme.typography.bodySmall.copy(color = PlexTextSecondary)
                        )
                    }
                }

                // Tactile Exit Button
                IconButton(
                    onClick = onExitDriverMode,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(PlexCard)
                        .border(1.dp, PlexBorder, CircleShape)
                        .testTag("exit_driver_mode_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Car Mode",
                        tint = PlexTextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Center Cockpit Display: Large Artwork + Big Glanceable Typography + Waveform Pulse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Square Album Artwork
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(220.dp)
                        .shadow(16.dp, RoundedCornerShape(18.dp), spotColor = Color.Black)
                        .clip(RoundedCornerShape(18.dp))
                        .background(PlexCard)
                        .border(1.dp, PlexBorder, RoundedCornerShape(18.dp))
                ) {
                    if (!currentTrack?.albumArtUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = currentTrack?.albumArtUrl,
                            contentDescription = "Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = PlexAmber,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(36.dp))

                // Song Info + Pulse
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currentTrack?.title ?: "No Media Playing",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PlexTextPrimary,
                            fontSize = 32.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${currentTrack?.artist ?: "StreamWave"} • ${currentTrack?.album ?: ""}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = PlexAmber,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Live Audio Pulse Spectrum (Automotive Audiophile)
                    Row(
                        modifier = Modifier.height(48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val heights = listOf(
                            0.4f * pulseHeight,
                            0.7f * pulseHeight,
                            0.95f * pulseHeight,
                            0.6f * pulseHeight,
                            0.85f * pulseHeight,
                            0.5f * pulseHeight,
                            0.9f * pulseHeight,
                            0.75f * pulseHeight,
                            0.4f * pulseHeight
                        )

                        heights.forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(7.dp)
                                    .height((48 * h).coerceAtLeast(6f).dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isPlaying) PlexAmber else PlexBorder)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "${formatMs(positionMs)} / ${formatMs(durationMs)}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PlexTextSecondary
                        )
                    }
                }
            }

            // Bottom Massive Automotive Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large Voice Mic
                IconButton(
                    onClick = onStartVoice,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(PlexCardElevated)
                        .border(1.dp, PlexBorder, CircleShape)
                        .testTag("driver_voice_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice",
                        tint = PlexAmber,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Previous (Large 74dp)
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier
                        .size(74.dp)
                        .clip(CircleShape)
                        .background(PlexCard)
                        .border(1.dp, PlexBorder, CircleShape)
                        .testTag("driver_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = PlexTextPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }

                // Giant Play / Pause Button (90dp touch target in Solid Plex Amber)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(92.dp)
                        .shadow(16.dp, CircleShape, spotColor = PlexAmber)
                        .clip(CircleShape)
                        .background(PlexAmber)
                        .clickable { onPlayPause() }
                        .testTag("driver_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(52.dp)
                    )
                }

                // Next (Large 74dp)
                IconButton(
                    onClick = onNext,
                    modifier = Modifier
                        .size(74.dp)
                        .clip(CircleShape)
                        .background(PlexCard)
                        .border(1.dp, PlexBorder, CircleShape)
                        .testTag("driver_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = PlexTextPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }

                // Large Favorite Star
                IconButton(
                    onClick = { currentTrack?.let { onToggleFavorite(it) } },
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(PlexCardElevated)
                        .border(1.dp, PlexBorder, CircleShape)
                        .testTag("driver_favorite_button")
                ) {
                    Icon(
                        imageVector = if (currentTrack?.isFavorite == true) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (currentTrack?.isFavorite == true) PlexAmber else PlexTextSecondary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}
