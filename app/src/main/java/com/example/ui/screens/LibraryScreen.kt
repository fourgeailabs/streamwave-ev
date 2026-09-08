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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.TrackEntity
import com.example.ui.components.formatMs
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
 * Authentic Plexamp Library Screen.
 * Displays tracks with lossless audio format badges, offline indicators,
 * fast searching, and quick playback activation.
 */
@Composable
fun LibraryScreen(
    tracks: List<TrackEntity>,
    currentTrack: TrackEntity?,
    isOfflineOnly: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onToggleOfflineOnly: () -> Unit,
    onPlayTrack: (TrackEntity) -> Unit,
    onToggleFavorite: (TrackEntity) -> Unit,
    onToggleDownload: (TrackEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredList = when (selectedFilter) {
        "FAVORITES" -> tracks.filter { it.isFavorite }
        "DOWNLOADS" -> tracks.filter { it.isDownloaded }
        else -> tracks
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PlexBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("library_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PlexCardElevated)
                        .border(1.dp, PlexBorder, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.LibraryMusic,
                        contentDescription = null,
                        tint = PlexAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Music Library",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PlexTextPrimary
                        )
                    )
                    Text(
                        text = "${tracks.size} tracks available",
                        style = MaterialTheme.typography.bodySmall.copy(color = PlexTextSecondary)
                    )
                }
            }

            // Offline Mode Road-Trip Switch
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PlexCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SignalCellularConnectedNoInternet0Bar,
                        contentDescription = null,
                        tint = if (isOfflineOnly) PlexGreen else PlexTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Offline",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isOfflineOnly) PlexGreen else PlexTextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = isOfflineOnly,
                        onCheckedChange = { onToggleOfflineOnly() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PlexGreen,
                            checkedTrackColor = PlexGreen.copy(alpha = 0.3f),
                            uncheckedThumbColor = PlexTextMuted,
                            uncheckedTrackColor = PlexCardElevated
                        ),
                        modifier = Modifier.testTag("offline_mode_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search tracks, artists, albums...", color = PlexTextMuted, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = PlexTextSecondary
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PlexCard,
                unfocusedContainerColor = PlexCard,
                focusedBorderColor = PlexAmber,
                unfocusedBorderColor = PlexBorder,
                focusedTextColor = PlexTextPrimary,
                unfocusedTextColor = PlexTextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_track_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Plexamp Filter Chips (ALL, FAVORITES, DOWNLOADS)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == "ALL",
                onClick = { selectedFilter = "ALL" },
                label = { Text("All Tracks (${tracks.size})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PlexAmber,
                    selectedLabelColor = Color.Black,
                    containerColor = PlexCard,
                    labelColor = PlexTextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilter == "ALL",
                    borderColor = PlexBorder,
                    selectedBorderColor = PlexAmber
                ),
                modifier = Modifier.testTag("filter_all")
            )

            FilterChip(
                selected = selectedFilter == "FAVORITES",
                onClick = { selectedFilter = "FAVORITES" },
                label = { Text("Favorites (${tracks.count { it.isFavorite }})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PlexAmber,
                    selectedLabelColor = Color.Black,
                    containerColor = PlexCard,
                    labelColor = PlexTextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilter == "FAVORITES",
                    borderColor = PlexBorder,
                    selectedBorderColor = PlexAmber
                ),
                modifier = Modifier.testTag("filter_favorites")
            )

            FilterChip(
                selected = selectedFilter == "DOWNLOADS",
                onClick = { selectedFilter = "DOWNLOADS" },
                label = { Text("Offline (${tracks.count { it.isDownloaded }})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PlexGreen,
                    selectedLabelColor = Color.Black,
                    containerColor = PlexCard,
                    labelColor = PlexTextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilter == "DOWNLOADS",
                    borderColor = PlexBorder,
                    selectedBorderColor = PlexGreen
                ),
                modifier = Modifier.testTag("filter_downloads")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tracks List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filteredList) { track ->
                val isSelected = currentTrack?.id == track.id

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) PlexCardElevated else PlexCard,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) PlexAmber else PlexBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onPlayTrack(track) }
                        .testTag("library_track_${track.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            // Square Album Thumbnail
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${track.artist} • ${formatMs(track.durationMs)}",
                                        color = PlexTextSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = PlexCardElevated
                                    ) {
                                        Text(
                                            text = "FLAC",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PlexAmber,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Right Action Buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onToggleDownload(track) },
                                modifier = Modifier.testTag("library_download_${track.id}")
                            ) {
                                Icon(
                                    imageVector = if (track.isDownloaded) Icons.Default.CloudDone else Icons.Default.Download,
                                    contentDescription = "Offline Cache",
                                    tint = if (track.isDownloaded) PlexGreen else PlexTextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { onToggleFavorite(track) },
                                modifier = Modifier.testTag("library_fav_${track.id}")
                            ) {
                                Icon(
                                    imageVector = if (track.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Favorite",
                                    tint = if (track.isFavorite) PlexAmber else PlexTextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
