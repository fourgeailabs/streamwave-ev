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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ServerEntity
import com.example.data.model.ServerType
import com.example.ui.theme.EvBorder
import com.example.ui.theme.EvCardElevated
import com.example.ui.theme.EvCardSurface
import com.example.ui.theme.EvCyan
import com.example.ui.theme.EvNeonGreen
import com.example.ui.theme.EvRed
import com.example.ui.theme.EvTextPrimary
import com.example.ui.theme.EvTextSecondary

@Composable
fun ServerManagementScreen(
    servers: List<ServerEntity>,
    onOpenPlexPinDialog: () -> Unit,
    onAddManualServer: (name: String, url: String, type: ServerType, tokenOrUser: String) -> Unit,
    onSetDefaultServer: (Long) -> Unit,
    onDeleteServer: (Long) -> Unit,
    onRefreshPings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("server_management_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Dns,
                    contentDescription = null,
                    tint = EvCyan,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Home Media Servers",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = EvTextPrimary
                        )
                    )
                    Text(
                        text = "Plex, Subsonic / Navidrome, and Jellyfin",
                        style = MaterialTheme.typography.bodyMedium.copy(color = EvTextSecondary)
                    )
                }
            }

            IconButton(
                onClick = onRefreshPings,
                modifier = Modifier.testTag("refresh_pings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Ping",
                    tint = EvCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Prominent TV PIN Action Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = EvCardElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, EvCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(EvCyan.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = null,
                            tint = EvCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Connect Plex via TV PIN",
                            fontWeight = FontWeight.Bold,
                            color = EvTextPrimary,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Link quickly using plex.tv/link 4-digit code",
                            color = EvTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                Button(
                    onClick = onOpenPlexPinDialog,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EvCyan,
                        contentColor = Color(0xFF021727)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("open_plex_pin_button")
                ) {
                    Text("Get PIN Code", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Manual Server Button
        OutlinedButton(
            onClick = { showAddDialog = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = EvCyan),
            border = androidx.compose.foundation.BorderStroke(1.dp, EvBorder),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().testTag("add_manual_server_button")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Subsonic / Jellyfin Server", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "CONFIGURED SERVERS (${servers.size})",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = EvCyan,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(servers, key = { it.id }) { server ->
                ServerItemCard(
                    server = server,
                    onSetDefault = { onSetDefaultServer(server.id) },
                    onDelete = { onDeleteServer(server.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddServerDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, url, type, auth ->
                onAddManualServer(name, url, type, auth)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ServerItemCard(
    server: ServerEntity,
    onSetDefault: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EvCardSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (server.isDefault) EvCyan else EvBorder
        ),
        modifier = Modifier.fillMaxWidth().testTag("server_card_${server.id}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF142037))
                ) {
                    Icon(
                        imageVector = when (server.type) {
                            ServerType.PLEX -> Icons.Default.Tv
                            else -> Icons.Default.Wifi
                        },
                        contentDescription = null,
                        tint = EvCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = server.name,
                            fontWeight = FontWeight.Bold,
                            color = EvTextPrimary,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (server.isDefault) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EvCyan.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    color = EvCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (server.isOnline) EvNeonGreen else EvRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${server.type.name} • ${server.lastPingMs} ms",
                            color = EvTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = server.serverUrl,
                        color = EvTextSecondary.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!server.isDefault) {
                    IconButton(onClick = onSetDefault) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Set Active",
                            tint = EvTextSecondary
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Server",
                        tint = EvRed.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun AddServerDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, url: String, type: ServerType, tokenOrUser: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ServerType.SUBSONIC) }
    var tokenOrUser by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = EvCardSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, EvBorder),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp).fillMaxWidth()
            ) {
                Text(
                    text = "Add Media Server",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = EvTextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(ServerType.SUBSONIC, ServerType.JELLYFIN, ServerType.PLEX).forEach { type ->
                        val isSel = type == selectedType
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) EvCyan.copy(alpha = 0.2f) else Color(0xFF142037),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) EvCyan else EvBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedType = type }
                        ) {
                            Text(
                                text = type.name,
                                color = if (isSel) EvCyan else EvTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Server Friendly Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EvCyan,
                        unfocusedBorderColor = EvBorder,
                        focusedTextColor = EvTextPrimary,
                        unfocusedTextColor = EvTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Server URL (e.g. https://192.168.1.100:4533)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EvCyan,
                        unfocusedBorderColor = EvBorder,
                        focusedTextColor = EvTextPrimary,
                        unfocusedTextColor = EvTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tokenOrUser,
                    onValueChange = { tokenOrUser = it },
                    label = { Text("Username or API Token") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EvCyan,
                        unfocusedBorderColor = EvBorder,
                        focusedTextColor = EvTextPrimary,
                        unfocusedTextColor = EvTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EvBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EvTextSecondary)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            if (url.isNotBlank()) {
                                onAdd(name, url, selectedType, tokenOrUser)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EvCyan,
                            contentColor = Color(0xFF021727)
                        )
                    ) {
                        Text("Save Server", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
