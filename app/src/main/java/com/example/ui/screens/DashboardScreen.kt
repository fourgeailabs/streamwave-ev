package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ServerEntity
import com.example.data.model.TrackEntity
import com.example.ui.theme.PlexAmber
import com.example.ui.theme.PlexAmberGlow
import com.example.ui.theme.PlexBackground
import com.example.ui.theme.PlexBorder
import com.example.ui.theme.PlexCard
import com.example.ui.theme.PlexCardElevated
import com.example.ui.theme.PlexGreen
import com.example.ui.theme.PlexPurple
import com.example.ui.theme.PlexSurface
import com.example.ui.theme.PlexTextMuted
import com.example.ui.theme.PlexTextPrimary
import com.example.ui.theme.PlexTextSecondary

/**
 * Authentic Plexamp Home Screen.
 * Features Plexamp's signature hubs: Recent Plays, Stations & Mixes (Library Radio, Time Travel, Sonic Adventure),
 * Heavy Rotation, and streamlined EV Car Cockpit controls.
 */
@Composable
fun DashboardScreen(
    activeServer: ServerEntity?,
    tracks: List<TrackEntity>,
    currentTrack: TrackEntity?,
    onPlayTrack: (TrackEntity) -> Unit,
    onStartVoice: () -> Unit,
    onOpenDriverMode: () -> Unit,
    onOpenPlexPin: () -> Unit,
    onOpenServers: () -> Unit,
    onOpenEqualizer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PlexBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 18.dp)
            .testTag("dashboard_screen")
    ) {
        // Plexamp Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PlexAmber)
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "StreamWave",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "STREAMWAVE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp,
                            color = PlexAmber
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (activeServer != null) PlexGreen else PlexAmber)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = activeServer?.let { "${it.name} • ${it.lastPingMs}ms" } ?: "StreamWave Audio Engine",
                            color = PlexTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            // Quick Car Mode HUD Trigger
            Button(
                onClick = onOpenDriverMode,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PlexCardElevated,
                    contentColor = PlexAmber
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("dashboard_driver_hud_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = PlexAmber
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Car Mode", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hands-Free EV Voice Command Banner (Audiophile Dark)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = PlexCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onStartVoice() }
                .testTag("dashboard_voice_banner")
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PlexCardElevated)
                            .border(1.dp, PlexAmber.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = PlexAmber,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Hands-Free Voice Commander",
                            fontWeight = FontWeight.Bold,
                            color = PlexTextPrimary,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "\"Play Next\", \"Show Lyrics\", \"Car Mode\", or \"Play [Track]\"",
                            color = PlexTextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PlexAmber)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Activate Voice",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Quick Hub Controls (Plex TV Link, Servers, EQ)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PlexCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenPlexPin() }
                    .testTag("dashboard_plex_link_button")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Tv, contentDescription = null, tint = PlexAmber, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Plex Link", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PlexTextPrimary)
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PlexCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenServers() }
                    .testTag("dashboard_servers_button")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Dns, contentDescription = null, tint = PlexAmber, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Servers", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PlexTextPrimary)
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PlexCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenEqualizer() }
                    .testTag("dashboard_eq_button")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Equalizer, contentDescription = null, tint = PlexAmber, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Equalizer", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PlexTextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Plexamp Hub: "RECENT PLAYS"
        Text(
            text = "RECENT PLAYS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PlexAmber,
            letterSpacing = 1.3.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(tracks.take(8)) { track ->
                val isSelected = currentTrack?.id == track.id

                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onPlayTrack(track) }
                        .testTag("track_item_${track.id}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PlexCard)
                            .border(
                                1.5.dp,
                                if (isSelected) PlexAmber else PlexBorder,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        if (!track.albumArtUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = track.albumArtUrl,
                                contentDescription = track.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = PlexAmber,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        // Play overlay indicator
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) PlexAmber else Color.Black.copy(alpha = 0.7f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = if (isSelected) Color.Black else PlexTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = track.title,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) PlexAmber else PlexTextPrimary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        color = PlexTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Plexamp Iconic Hub: "STATIONS & MIXES"
        Text(
            text = "STATIONS & MIXES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PlexAmber,
            letterSpacing = 1.3.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Station 1: Library Radio
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PlexCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { tracks.shuffled().firstOrNull()?.let { onPlayTrack(it) } }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PlexAmber.copy(alpha = 0.15f))
                    ) {
                        Icon(Icons.Default.Radio, contentDescription = null, tint = PlexAmber, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Library Radio", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PlexTextPrimary)
                    Text("Infinite mix from library", fontSize = 11.sp, color = PlexTextSecondary)
                }
            }

            // Station 2: Sonic Adventure
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PlexCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { tracks.lastOrNull()?.let { onPlayTrack(it) } }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PlexPurple.copy(alpha = 0.15f))
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PlexPurple, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Sonic Flow", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PlexTextPrimary)
                    Text("Acoustic sonic path", fontSize = 11.sp, color = PlexTextSecondary)
                }
            }

            // Station 3: Time Travel
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PlexCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { tracks.firstOrNull()?.let { onPlayTrack(it) } }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PlexGreen.copy(alpha = 0.15f))
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = PlexGreen, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Time Travel", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PlexTextPrimary)
                    Text("Decade & era journey", fontSize = 11.sp, color = PlexTextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Plexamp Hub: "HEAVY ROTATION"
        Text(
            text = "HEAVY ROTATION",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PlexAmber,
            letterSpacing = 1.3.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tracks.drop(2).take(5).forEach { track ->
                val isSelected = currentTrack?.id == track.id

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) PlexCardElevated else PlexCard,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) PlexAmber else PlexBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onPlayTrack(track) }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PlexSurface)
                            ) {
                                if (!track.albumArtUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = track.albumArtUrl,
                                        contentDescription = track.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = PlexAmber,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = track.title,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) PlexAmber else PlexTextPrimary,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${track.artist} • ${track.album}",
                                    color = PlexTextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = if (isSelected) PlexAmber else PlexTextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
