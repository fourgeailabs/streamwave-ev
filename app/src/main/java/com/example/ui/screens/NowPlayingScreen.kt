package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.LyricLine
import com.example.data.model.RepeatMode
import com.example.data.model.SoundPreset
import com.example.data.model.TrackEntity
import com.example.ui.components.PlexampWaveformScrubber
import com.example.ui.components.SyncedLyricsViewer
import com.example.ui.theme.PlexAmber
import com.example.ui.theme.PlexAmberGlow
import com.example.ui.theme.PlexBackground
import com.example.ui.theme.PlexBorder
import com.example.ui.theme.PlexCard
import com.example.ui.theme.PlexCardElevated
import com.example.ui.theme.PlexGreen
import com.example.ui.theme.PlexRed
import com.example.ui.theme.PlexSurface
import com.example.ui.theme.PlexTextMuted
import com.example.ui.theme.PlexTextPrimary
import com.example.ui.theme.PlexTextSecondary

/**
 * Authentic Plexamp Now Playing Screen.
 * Incorporates Plexamp's iconic deep obsidian aura, square album presentation,
 * lossless format pill badge, loudness waveform scrubber, and tactile transport controls.
 */
@Composable
fun NowPlayingScreen(
    currentTrack: TrackEntity?,
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    isShuffle: Boolean,
    repeatMode: RepeatMode,
    soundPreset: SoundPreset,
    lyrics: List<LyricLine>,
    activeLyricIndex: Int,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleFavorite: (TrackEntity) -> Unit,
    onToggleDownload: (TrackEntity) -> Unit,
    onOpenEqualizer: () -> Unit,
    onOpenDriverMode: () -> Unit,
    onStartVoice: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLyricsView by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF161512), // Subtle warm amber-tinted shadow
                        PlexSurface,
                        PlexBackground,
                        PlexBackground
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 14.dp)
            .testTag("now_playing_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Plexamp Minimalist Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("now_playing_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = PlexTextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Center Library / Format Indicator
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLAYING FROM PLEX",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp,
                        color = PlexAmber
                    )
                    Text(
                        text = currentTrack?.album ?: "StreamWave Audio",
                        fontSize = 12.sp,
                        color = PlexTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Right Auxiliary Header (Car Mode & Voice)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenDriverMode,
                        modifier = Modifier.testTag("driver_mode_hud_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Driver Mode",
                            tint = PlexAmber,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = onStartVoice,
                        modifier = Modifier.testTag("now_playing_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Control",
                            tint = PlexTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Center Display: Plexamp Album Art OR Synced Karaoke Lyrics
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = showLyricsView,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "LyricsArtTransition"
                ) { isLyrics ->
                    if (isLyrics) {
                        SyncedLyricsViewer(
                            lyrics = lyrics,
                            activeLineIndex = activeLyricIndex,
                            onLineClick = onSeekTo
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Square Album Artwork with Plexamp deep drop shadow
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(270.dp)
                                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(16.dp), spotColor = Color.Black)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PlexCard)
                                    .border(1.dp, PlexBorder, RoundedCornerShape(16.dp))
                            ) {
                                if (!currentTrack?.albumArtUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = currentTrack?.albumArtUrl,
                                        contentDescription = "Album Art",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = PlexAmber,
                                        modifier = Modifier.size(96.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Plexamp Audiophile Format Badge
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = PlexCardElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(PlexAmber)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "FLAC • 24-bit / 96 kHz • Lossless",
                                        color = PlexTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.6.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Track Title, Artist, and Favorite / Download Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentTrack?.title ?: "Select a Track",
                        fontWeight = FontWeight.Bold,
                        color = PlexTextPrimary,
                        fontSize = 22.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentTrack?.artist ?: "Unknown Artist",
                        color = PlexTextSecondary,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { currentTrack?.let { onToggleDownload(it) } },
                        modifier = Modifier.testTag("download_track_button")
                    ) {
                        Icon(
                            imageVector = if (currentTrack?.isDownloaded == true) Icons.Default.CloudDone else Icons.Default.Download,
                            contentDescription = "Offline Cache",
                            tint = if (currentTrack?.isDownloaded == true) PlexGreen else PlexTextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = { currentTrack?.let { onToggleFavorite(it) } },
                        modifier = Modifier.testTag("favorite_track_button")
                    ) {
                        Icon(
                            imageVector = if (currentTrack?.isFavorite == true) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (currentTrack?.isFavorite == true) PlexAmber else PlexTextSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Plexamp Signature Loudness Waveform Scrubber
            PlexampWaveformScrubber(
                positionMs = positionMs,
                durationMs = durationMs,
                trackSeed = currentTrack?.title ?: "streamwave",
                onSeekTo = onSeekTo,
                activeColor = PlexAmber,
                inactiveColor = PlexBorder,
                modifier = Modifier.testTag("playback_seek_slider")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Plexamp Primary Transport Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleShuffle,
                    modifier = Modifier.testTag("shuffle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffle) PlexAmber else PlexTextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier.testTag("previous_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = PlexTextPrimary,
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Plexamp Signature Gold Play/Pause Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(70.dp)
                        .shadow(12.dp, CircleShape, spotColor = PlexAmber)
                        .clip(CircleShape)
                        .background(PlexAmber)
                        .clickable { onPlayPause() }
                        .testTag("play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(38.dp)
                    )
                }

                IconButton(
                    onClick = onNext,
                    modifier = Modifier.testTag("next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = PlexTextPrimary,
                        modifier = Modifier.size(38.dp)
                    )
                }

                IconButton(
                    onClick = onToggleRepeat,
                    modifier = Modifier.testTag("repeat_button")
                ) {
                    Icon(
                        imageVector = when (repeatMode) {
                            RepeatMode.ONE -> Icons.Default.RepeatOne
                            else -> Icons.Default.Repeat
                        },
                        contentDescription = "Repeat",
                        tint = if (repeatMode != RepeatMode.OFF) PlexAmber else PlexTextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Plexamp Bottom Tool Bar (Lyrics, EQ, Preset)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Synced Lyrics Toggle Button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (showLyricsView) PlexAmber.copy(alpha = 0.2f) else PlexCard,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (showLyricsView) PlexAmber else PlexBorder
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showLyricsView = !showLyricsView }
                        .testTag("toggle_lyrics_view")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Subtitles,
                            contentDescription = "Lyrics",
                            tint = if (showLyricsView) PlexAmber else PlexTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Lyrics",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (showLyricsView) PlexAmber else PlexTextSecondary
                        )
                    }
                }

                // Sound Preset / Equalizer Trigger
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PlexCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenEqualizer() }
                        .testTag("now_playing_eq_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = "Equalizer",
                            tint = PlexAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = soundPreset.displayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = PlexTextPrimary
                        )
                    }
                }
            }
        }
    }
}
