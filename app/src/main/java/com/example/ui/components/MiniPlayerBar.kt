package com.example.ui.components

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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.TrackEntity
import com.example.ui.theme.PlexAmber
import com.example.ui.theme.PlexBackground
import com.example.ui.theme.PlexBorder
import com.example.ui.theme.PlexCard
import com.example.ui.theme.PlexCardElevated
import com.example.ui.theme.PlexTextMuted
import com.example.ui.theme.PlexTextPrimary
import com.example.ui.theme.PlexTextSecondary

/**
 * Authentic Plexamp Mini Player Dock.
 * Features a razor-thin gold progress bar, square album art thumbnail,
 * clean typography, and tactile transport controls.
 */
@Composable
fun MiniPlayerBar(
    currentTrack: TrackEntity?,
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    onBarClick: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onOpenLyrics: () -> Unit,
    onVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentTrack == null) return

    val progress = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    Surface(
        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
        color = PlexCardElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder),
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp), spotColor = Color.Black)
            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            .clickable { onBarClick() }
            .testTag("mini_player_bar")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Razor-thin Plexamp Gold progress line
            LinearProgressIndicator(
                progress = { progress },
                color = PlexAmber,
                trackColor = PlexCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Album Art & Track Metadata
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PlexCard)
                            .border(1.dp, PlexBorder, RoundedCornerShape(8.dp))
                    ) {
                        if (!currentTrack.albumArtUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = currentTrack.albumArtUrl,
                                contentDescription = "Album Art",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = PlexAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = currentTrack.title,
                            fontWeight = FontWeight.SemiBold,
                            color = PlexTextPrimary,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentTrack.artist,
                            color = PlexTextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onOpenLyrics,
                        modifier = Modifier.testTag("mini_lyrics_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Subtitles,
                            contentDescription = "Synced Lyrics",
                            tint = PlexTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onVoiceClick,
                        modifier = Modifier.testTag("mini_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Control",
                            tint = PlexTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier.testTag("mini_prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = PlexTextPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Plexamp Play/Pause circular button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PlexAmber)
                            .clickable { onPlayPause() }
                            .testTag("mini_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.testTag("mini_next_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = PlexTextPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}
