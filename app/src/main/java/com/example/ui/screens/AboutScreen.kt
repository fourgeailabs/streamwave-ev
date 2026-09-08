package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EvBorder
import com.example.ui.theme.EvCardElevated
import com.example.ui.theme.EvCardSurface
import com.example.ui.theme.EvCyan
import com.example.ui.theme.EvNeonGreen
import com.example.ui.theme.EvTextPrimary
import com.example.ui.theme.EvTextSecondary

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val openUrl = { url: String ->
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Gracefully ignore if no browser available
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("about_screen")
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("about_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = EvCyan
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = EvCyan,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = EvTextPrimary
                    )
                )
                Text(
                    text = "StreamWave Audio Platform",
                    style = MaterialTheme.typography.bodyMedium.copy(color = EvTextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Hero Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = EvCardElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, EvBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(EvCyan.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = EvCyan,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "StreamWave",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = EvTextPrimary
                    )
                )

                Text(
                    text = "High-Fidelity Server Streaming Client",
                    style = MaterialTheme.typography.bodyMedium.copy(color = EvCyan)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F1B2E)
                ) {
                    Text(
                        text = "Version 1.02.00",
                        color = EvTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Creator Section - FourgeAI LABS (Mandatory clickable)
        Text(
            text = "APP CREATOR",
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
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { openUrl("https://github.com/fourgeailabs") }
                .testTag("creator_link")
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EvNeonGreen.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = EvNeonGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "FourgeAI LABS",
                            fontWeight = FontWeight.Bold,
                            color = EvTextPrimary,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "https://github.com/fourgeailabs",
                            color = EvCyan,
                            fontSize = 13.sp
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Open GitHub",
                    tint = EvCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Repository Link
        Text(
            text = "SOURCE CODE REPOSITORY",
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
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { openUrl("https://github.com/fourgeailabs/streamwave-ev") }
                .testTag("app_repo_link")
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EvCyan.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = EvCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "StreamWave Repository",
                            fontWeight = FontWeight.Bold,
                            color = EvTextPrimary,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "github.com/fourgeailabs/streamwave",
                            color = EvTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Open App Repo",
                    tint = EvCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // EV Acoustic Specs
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EvCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, EvBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = EvCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Vehicle Integration Specs",
                        fontWeight = FontWeight.Bold,
                        color = EvTextPrimary,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "• Target Vehicle: Chevrolet Equinox EV & GM Ultium Cockpit\n• Display Aspect: 17.7-inch Infotainment Dual-Display Native\n• Audio Engine: Lossless FLAC / OGG / MP3 with Dynamic Audio Focus\n• Sync Protocol: Real-Time LRC Millisecond Highlighting\n• Auth Standard: Plex TV PIN OAuth 2.0 (plex.tv/api/v2/pins)",
                    color = EvTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
