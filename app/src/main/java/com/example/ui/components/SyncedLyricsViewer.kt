package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LyricLine
import com.example.ui.theme.PlexAmber
import com.example.ui.theme.PlexAmberGlow
import com.example.ui.theme.PlexBackground
import com.example.ui.theme.PlexTextMuted
import com.example.ui.theme.PlexTextPrimary
import com.example.ui.theme.PlexTextSecondary

/**
 * Authentic Plexamp Synchronized Lyrics Screen.
 * Displays real-time karaoke lyrics with glowing active typography,
 * smooth centered auto-scrolling, and interactive tap-to-seek playback jump.
 */
@Composable
fun SyncedLyricsViewer(
    lyrics: List<LyricLine>,
    activeLineIndex: Int,
    onLineClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Smoothly scroll to keep active lyric line centered
    LaunchedEffect(activeLineIndex) {
        if (activeLineIndex in lyrics.indices) {
            val targetScroll = (activeLineIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetScroll)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PlexBackground)
            .testTag("synced_lyrics_viewer")
    ) {
        if (lyrics.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = PlexTextMuted,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "No Synchronized Lyrics Available",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PlexTextSecondary
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Ensure .lrc file is in track folder or embedded in metadata",
                    style = MaterialTheme.typography.bodySmall.copy(color = PlexTextMuted),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(top = 80.dp, bottom = 140.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(lyrics) { index, line ->
                    val isActive = index == activeLineIndex

                    val textColor by animateColorAsState(
                        targetValue = if (isActive) PlexAmber else PlexTextMuted,
                        animationSpec = tween(300),
                        label = "lyricTextColor"
                    )

                    val fontSize = if (isActive) 24.sp else 18.sp
                    val fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Normal

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onLineClick(line.timestampMs) }
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = line.text,
                            color = textColor,
                            fontSize = fontSize,
                            fontWeight = fontWeight,
                            lineHeight = if (isActive) 32.sp else 26.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isActive) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatMs(line.timestampMs),
                                color = PlexAmber.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
