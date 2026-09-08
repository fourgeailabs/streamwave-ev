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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SoundPreset
import com.example.ui.theme.PlexAmber
import com.example.ui.theme.PlexAmberGlow
import com.example.ui.theme.PlexBackground
import com.example.ui.theme.PlexBorder
import com.example.ui.theme.PlexCard
import com.example.ui.theme.PlexCardElevated
import com.example.ui.theme.PlexTextMuted
import com.example.ui.theme.PlexTextPrimary
import com.example.ui.theme.PlexTextSecondary

/**
 * Plexamp Audiophile 5-Band Equalizer & Cabin Soundstage.
 * Incorporates parametric frequency sliders, EV acoustic presets, and gain readouts.
 */
@Composable
fun EquinoxEqualizerView(
    activePreset: SoundPreset,
    onSelectPreset: (SoundPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    var band60 by remember { mutableStateOf(4f) }
    var band230 by remember { mutableStateOf(2f) }
    var band910 by remember { mutableStateOf(0f) }
    var band3k by remember { mutableStateOf(3f) }
    var band14k by remember { mutableStateOf(5f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PlexBackground)
            .verticalScroll(rememberScrollState())
            .padding(22.dp)
            .testTag("equinox_equalizer_view")
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PlexCardElevated)
                    .border(1.dp, PlexBorder, RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Equalizer,
                    contentDescription = null,
                    tint = PlexAmber,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Acoustic Equalizer",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PlexTextPrimary
                    )
                )
                Text(
                    text = "Audiophile DSP & EV Cabin Soundstage",
                    style = MaterialTheme.typography.bodySmall.copy(color = PlexTextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Soundstage Presets
        Text(
            text = "SOUNDSTAGE PROFILES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PlexAmber,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SoundPreset.entries.forEach { preset ->
                val isSelected = preset == activePreset
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) PlexAmber else PlexCard,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) PlexAmber else PlexBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectPreset(preset) }
                        .testTag("preset_${preset.name.lowercase()}")
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = preset.displayName,
                            color = if (isSelected) Color.Black else PlexTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5-Band Sliders Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PlexCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, PlexBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "PARAMETRIC FREQUENCY BANDS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlexAmber,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                EqualizerBandRow(label = "60 Hz (Sub Bass)", value = band60, onValueChange = { band60 = it })
                EqualizerBandRow(label = "230 Hz (Mid Bass)", value = band230, onValueChange = { band230 = it })
                EqualizerBandRow(label = "910 Hz (Vocals)", value = band910, onValueChange = { band910 = it })
                EqualizerBandRow(label = "3 kHz (Presence)", value = band3k, onValueChange = { band3k = it })
                EqualizerBandRow(label = "14 kHz (Air/Treble)", value = band14k, onValueChange = { band14k = it })
            }
        }
    }
}

@Composable
private fun EqualizerBandRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = PlexTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(
                text = "${if (value > 0) "+" else ""}${value.toInt()} dB",
                color = PlexAmber,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -10f..10f,
            steps = 19,
            colors = SliderDefaults.colors(
                thumbColor = PlexAmber,
                activeTrackColor = PlexAmber,
                inactiveTrackColor = PlexCardElevated
            )
        )
    }
}
