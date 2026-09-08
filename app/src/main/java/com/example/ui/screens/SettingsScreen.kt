package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EvBorder
import com.example.ui.theme.EvCardSurface
import com.example.ui.theme.EvCyan
import com.example.ui.theme.EvRed
import com.example.ui.theme.EvTextPrimary
import com.example.ui.theme.EvTextSecondary

@Composable
fun SettingsScreen(
    cacheSize: String,
    onClearCache: () -> Unit,
    onNavigateWhatsNew: () -> Unit,
    onNavigateAbout: () -> Unit,
    onNavigateServers: () -> Unit,
    onNavigateEqualizer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var autoDriverMode by remember { mutableStateOf(true) }
    var highBitrateFlac by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("settings_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = EvCyan,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = EvTextPrimary
                    )
                )
                Text(
                    text = "Equinox EV Audio Configuration & Diagnostics",
                    style = MaterialTheme.typography.bodyMedium.copy(color = EvTextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Offline Caching & Storage
        Text(
            text = "STORAGE & OFFLINE CACHE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = EvCyan,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EvCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, EvBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = EvCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Downloaded Audio Storage",
                                fontWeight = FontWeight.Bold,
                                color = EvTextPrimary,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Cached tracks for zero-cellular dead zones",
                                color = EvTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Text(
                        text = cacheSize,
                        color = EvCyan,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onClearCache,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EvRed.copy(alpha = 0.2f),
                        contentColor = EvRed
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("clear_cache_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear Offline Audio Cache", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Vehicle Driving Features
        Text(
            text = "EV DRIVING INTEGRATIONS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = EvCyan,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EvCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, EvBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = EvCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Auto-Trigger Driver Mode",
                                fontWeight = FontWeight.Bold,
                                color = EvTextPrimary,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Enlarge controls when vehicle speed > 5 mph",
                                color = EvTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Switch(
                        checked = autoDriverMode,
                        onCheckedChange = { autoDriverMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EvCyan,
                            checkedTrackColor = EvCyan.copy(alpha = 0.4f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.HighQuality,
                            contentDescription = null,
                            tint = EvCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Lossless FLAC Streaming",
                                fontWeight = FontWeight.Bold,
                                color = EvTextPrimary,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Stream uncompressed 24-bit audio from home server",
                                color = EvTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Switch(
                        checked = highBitrateFlac,
                        onCheckedChange = { highBitrateFlac = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EvCyan,
                            checkedTrackColor = EvCyan.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation Menu (What's New & About)
        Text(
            text = "APP & SYSTEM INFO",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = EvCyan,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        SettingsNavRow(
            title = "What's New",
            subtitle = "Recent update release notes and historical log",
            icon = Icons.Default.NewReleases,
            testTag = "nav_whats_new",
            onClick = onNavigateWhatsNew
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsNavRow(
            title = "Acoustic Equalizer",
            subtitle = "Configure Equinox EV cabin soundstage presets",
            icon = Icons.Default.Equalizer,
            testTag = "nav_equalizer",
            onClick = onNavigateEqualizer
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsNavRow(
            title = "Home Media Servers",
            subtitle = "Manage Plex TV PIN, Subsonic, and Jellyfin",
            icon = Icons.Default.Dns,
            testTag = "nav_servers",
            onClick = onNavigateServers
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsNavRow(
            title = "About",
            subtitle = "FourgeAI LABS creator profile and repository",
            icon = Icons.Default.Info,
            testTag = "nav_about",
            onClick = onNavigateAbout
        )
    }
}

@Composable
fun SettingsNavRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EvCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, EvBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EvCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = EvTextPrimary,
                        fontSize = 16.sp
                    )
                    Text(
                        text = subtitle,
                        color = EvTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = EvTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
