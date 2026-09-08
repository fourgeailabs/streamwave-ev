package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlexAmber
import com.example.ui.theme.PlexBackground
import com.example.ui.theme.PlexBorder
import com.example.ui.theme.PlexCard
import com.example.ui.theme.PlexCardElevated
import com.example.ui.theme.PlexGreen
import com.example.ui.theme.PlexTextMuted
import com.example.ui.theme.PlexTextPrimary
import com.example.ui.theme.PlexTextSecondary

data class ReleaseItem(
    val version: String,
    val releaseDate: String,
    val isLatest: Boolean,
    val highlights: List<String>
)

val RELEASE_HISTORY = listOf(
    ReleaseItem(
        version = "1.02.00",
        releaseDate = "Current Release",
        isLatest = true,
        highlights = listOf(
            "StreamWave Global Branding: Rebranded the application globally to StreamWave across all UI displays, headers, vehicle HUD, and service integrations.",
            "Plex TV PIN Sign-In Engine Fix: Fixed TV PIN generation to request real 4-digit codes from plex.tv, eliminating 25-character hash overflow and enabling seamless linking at plex.tv/link.",
            "Direct Plex Token Sign-In: Added a dedicated tab in the link dialog to connect directly using an X-Plex-Token or custom server IP/URL.",
            "One-Tap plex.tv/link Launcher: Integrated a direct link button to immediately open the Plex linking page in your device browser.",
            "Persistent Client Identifier: Stable hardware client identity saved to persistent storage for continuous Plex server authentication.",
            "Null-Safe Server Discovery: Fixed JSON auth token extraction and multi-server resource parsing for rock-solid connection reliability."
        )
    ),
    ReleaseItem(
        version = "1.01.00",
        releaseDate = "Previous Release",
        isLatest = false,
        highlights = listOf(
            "Audiophile Visual Architecture: Premium visual design with signature dark obsidian and warm amber color palette.",
            "Interactive Loudness Waveform Scrubber: Real-time amplitude bar profile with smooth seek-scrubbing and precise timestamps.",
            "StreamWave Home Hubs: Recent Plays, Stations & Mixes (Library Radio, Sonic Flow, Time Travel), and Heavy Rotation carousels.",
            "StreamWave EV Car Mode Overhaul: Distraction-free high-contrast cockpit with 88dp+ tactile touch targets, live audio spectrum pulse, and hands-free voice trigger.",
            "Lossless Audiophile Format Badges: FLAC 24-bit / 96 kHz lossless and Direct Play badges throughout Now Playing and Library screens.",
            "Synchronized Karaoke Lyrics: Glowing active line typography, smooth centered auto-scroll, and tap-to-seek playback jump.",
            "Plex TV PIN Link: 4-digit TV authorization via plex.tv/link with auto-polling."
        )
    ),
    ReleaseItem(
        version = "1.00.00",
        releaseDate = "Initial Production Release",
        isLatest = false,
        highlights = listOf(
            "Plex TV PIN Code Sign-In: 4-digit TV pairing via plex.tv/link with auto-poll discovery",
            "Equinox EV & Automotive Landscape Architecture: 17.7-inch ultrawide native layout with Driver Mode HUD",
            "Synchronized Lyrics Engine: High-precision real-time .lrc auto-scrolling with tap-to-seek playback jump",
            "Multi-Server Home Streaming: Native integration for Plex, Subsonic / Navidrome, and Jellyfin",
            "Offline Road Trip Caching: Download albums and songs locally with 'Downloaded Only' vehicle toggle",
            "Hands-Free Voice Command Assistant: Voice recognition for play, skip, driver mode, and favorite commands",
            "Acoustic Soundstage Presets: Tailored 5-band EQ for Equinox EV cabin acoustics and Bose audio systems"
        )
    ),
    ReleaseItem(
        version = "0.90.00",
        releaseDate = "Beta Architecture Preview",
        isLatest = false,
        highlights = listOf(
            "Initial Audio Core: Background MediaPlayer engine with audio focus management",
            "Room Database Schema: Offline track indexing and server credential storage",
            "Responsive Navigation: Adaptive layout transitions between phones, tablets, and EV screens"
        )
    ),
    ReleaseItem(
        version = "0.80.00",
        releaseDate = "Alpha Milestone",
        isLatest = false,
        highlights = listOf(
            "Prototype TV PIN API: Communication with plex.tv/api/v2/pins endpoint",
            "Basic LRC Parser: Initial time-tag parsing and milliseconds offset support",
            "FourgeAI LABS Brand Identity: Dark-mode automotive theme tokens"
        )
    )
)

@Composable
fun WhatsNewScreen(
    expandedIndex: Int,
    onToggleAccordion: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PlexBackground)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("whats_new_screen")
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("whats_new_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = PlexAmber
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.NewReleases,
                contentDescription = null,
                tint = PlexAmber,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "What's New",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PlexTextPrimary
                    )
                )
                Text(
                    text = "StreamWave Release Notes & History",
                    style = MaterialTheme.typography.bodyMedium.copy(color = PlexTextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "RELEASE HISTORY (TAP TO EXPAND)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PlexAmber,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        RELEASE_HISTORY.forEachIndexed { index, item ->
            val isExpanded = index == expandedIndex
            val rotationAngle by animateFloatAsState(
                targetValue = if (isExpanded) 180f else 0f,
                label = "arrowRotation"
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isExpanded) PlexCardElevated else PlexCard
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isExpanded) PlexAmber else PlexBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onToggleAccordion(index) }
                    .testTag("whats_new_item_$index")
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "v${item.version}",
                                fontWeight = FontWeight.Bold,
                                color = if (item.isLatest) PlexAmber else PlexTextPrimary,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            if (item.isLatest) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = PlexGreen.copy(alpha = 0.2f),
                                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                                ) {
                                    Text(
                                        text = "CURRENT UPDATE",
                                        color = PlexGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            } else {
                                Text(
                                    text = item.releaseDate,
                                    color = PlexTextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = if (isExpanded) PlexAmber else PlexTextSecondary,
                            modifier = Modifier.rotate(rotationAngle)
                        )
                    }

                    // Accordion Dropdown Content
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(PlexBorder)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            item.highlights.forEach { note ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "• ",
                                        color = PlexAmber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = note,
                                        color = PlexTextPrimary,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
