package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.remote.PlexPinState
import com.example.ui.theme.PlexAmber
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
 * StreamWave Plex TV 4-Digit PIN & Direct Authentication Dialog.
 * Connects the vehicle or device to plex.tv/link using 4-character TV authorization
 * or direct Plex Token.
 */
@Composable
fun PlexPinDialog(
    pinState: PlexPinState?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    onSimulateSuccess: () -> Unit,
    onDirectTokenConnect: (name: String, url: String, token: String) -> Unit = { _, _, _ -> }
) {
    val uriHandler = LocalUriHandler.current
    var showManualTokenTab by remember { mutableStateOf(false) }
    var directServerName by remember { mutableStateOf("Plex Home Server") }
    var directServerUrl by remember { mutableStateOf("https://plex.tv") }
    var directToken by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PlexCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("plex_pin_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "Plex TV Sign-In",
                            tint = PlexAmber,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "StreamWave Plex Link",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = PlexTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_plex_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = PlexTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Selector: TV PIN vs Direct Token
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PlexSurface)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!showManualTokenTab) PlexAmber.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable { showManualTokenTab = false }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "TV PIN Code",
                            color = if (!showManualTokenTab) PlexAmber else PlexTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (showManualTokenTab) PlexAmber.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable { showManualTokenTab = true }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Direct Plex Token",
                            color = if (showManualTokenTab) PlexAmber else PlexTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (showManualTokenTab) {
                    // Manual Direct Token Sign-In Form
                    Text(
                        text = "Enter your Plex server details or X-Plex-Token directly:",
                        style = MaterialTheme.typography.bodySmall.copy(color = PlexTextSecondary),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = directServerName,
                        onValueChange = { directServerName = it },
                        label = { Text("Server Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PlexAmber,
                            unfocusedBorderColor = PlexBorder,
                            focusedTextColor = PlexTextPrimary,
                            unfocusedTextColor = PlexTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("plex_direct_name_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = directServerUrl,
                        onValueChange = { directServerUrl = it },
                        label = { Text("Server URL or IP (or https://plex.tv)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PlexAmber,
                            unfocusedBorderColor = PlexBorder,
                            focusedTextColor = PlexTextPrimary,
                            unfocusedTextColor = PlexTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("plex_direct_url_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = directToken,
                        onValueChange = { directToken = it },
                        label = { Text("Plex Auth Token (X-Plex-Token)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PlexAmber,
                            unfocusedBorderColor = PlexBorder,
                            focusedTextColor = PlexTextPrimary,
                            unfocusedTextColor = PlexTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("plex_direct_token_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (directToken.isNotBlank()) {
                                onDirectTokenConnect(directServerName, directServerUrl, directToken)
                            }
                        },
                        enabled = directToken.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PlexAmber,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_direct_token_button")
                    ) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Connect with Token", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Standard TV PIN Flow
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = PlexAmber,
                            modifier = Modifier
                                .size(44.dp)
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Requesting secure TV PIN code from plex.tv...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = PlexTextSecondary),
                            textAlign = TextAlign.Center
                        )
                    } else if (pinState != null) {
                        if (pinState.isLinked) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Linked",
                                tint = PlexGreen,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Plex Account Linked!",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = PlexGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Your Home Media Server is now connected to StreamWave.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = PlexTextSecondary),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = PlexAmber, contentColor = Color.Black),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("done_plex_button")
                            ) {
                                Text("Start Streaming Music", fontWeight = FontWeight.Bold)
                            }
                        } else if (!pinState.errorMessage.isNullOrEmpty()) {
                            // Error display
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = PlexRed,
                                modifier = Modifier.size(46.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "PIN Request Failed",
                                color = PlexRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = pinState.errorMessage,
                                color = PlexTextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onRetry,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PlexAmber)
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Retry")
                                }
                                Button(
                                    onClick = onSimulateSuccess,
                                    colors = ButtonDefaults.buttonColors(containerColor = PlexAmber, contentColor = Color.Black),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Use Demo Link")
                                }
                            }
                        } else {
                            Text(
                                text = "Enter this 4-digit code on your phone, tablet, or laptop:",
                                style = MaterialTheme.typography.bodyMedium.copy(color = PlexTextSecondary),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Large 4-digit blocks
                            val code = pinState.code.ifBlank { "----" }
                            val displayCode = code.take(4)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                displayCode.forEach { char ->
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(PlexSurface)
                                            .border(2.dp, PlexAmber, RoundedCornerShape(12.dp))
                                    ) {
                                        Text(
                                            text = char.toString(),
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontFamily = FontFamily.Monospace,
                                            color = PlexAmber
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // One-tap launch button to open plex.tv/link
                            Button(
                                onClick = {
                                    try {
                                        uriHandler.openUri("https://plex.tv/link")
                                    } catch (_: Exception) {}
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PlexAmber,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("open_plex_link_web_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInBrowser,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Open plex.tv/link to Authorize", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Link code expires in ${pinState.expiresInSeconds / 60} minutes",
                                color = PlexTextMuted,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = PlexAmber,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Auto-detecting authorization from plex.tv...",
                                    color = PlexTextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Test / Demo bypass button
                            OutlinedButton(
                                onClick = onSimulateSuccess,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PlexTextSecondary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("simulate_plex_link_button")
                            ) {
                                Text("Instant Connect Demo Server", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
