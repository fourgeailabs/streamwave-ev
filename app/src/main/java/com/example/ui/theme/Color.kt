package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// StreamWave Dark Audiophile Color Palette
val PlexAmber = Color(0xFFE5A00D)          // StreamWave signature warm amber
val PlexAmberGlow = Color(0xFFFFB82E)      // Bright amber highlight
val PlexAmberMuted = Color(0x33E5A00D)     // Subtle amber tint
val PlexGold = Color(0xFFF29D00)

// Audiophile Midnight Black Surfaces
val PlexBackground = Color(0xFF0A0A0C)     // Deep obsidian black
val PlexSurface = Color(0xFF121316)        // Clean dark charcoal
val PlexCard = Color(0xFF18191E)           // Elevated surface
val PlexCardElevated = Color(0xFF22232B)   // High elevation card
val PlexBorder = Color(0xFF2A2B35)         // Subtle divider

// Functional & Status Accents
val PlexGreen = Color(0xFF2ECC71)          // Offline downloaded / success
val PlexRed = Color(0xFFE74C3C)            // Favorite / danger
val PlexBlue = Color(0xFF3498DB)           // Direct stream indicator
val PlexPurple = Color(0xFF9B59B6)         // Sonic adventure badge

// Audiophile Typography Grays
val PlexTextPrimary = Color(0xFFF5F5F7)    // Studio crisp white
val PlexTextSecondary = Color(0xFF989AA4)  // Muted metadata
val PlexTextMuted = Color(0xFF656773)      // Subtle timestamps and dividers

// Aliases for legacy EV variables to ensure 100% backward-compatibility
val EvCyan = PlexAmber
val EvCyanGlow = PlexAmberGlow
val EvDarkNavy = PlexBackground
val EvObsidian = PlexSurface
val EvCardSurface = PlexCard
val EvCardElevated = PlexCardElevated
val EvBorder = PlexBorder

val EvElectricBlue = PlexAmber
val EvNeonGreen = PlexGreen
val EvAmber = PlexAmber
val EvRed = PlexRed

val EvTextPrimary = PlexTextPrimary
val EvTextSecondary = PlexTextSecondary
val EvTextMuted = PlexTextMuted

// Material 3 Dark Colors
val EvPrimary = PlexAmber
val EvOnPrimary = Color(0xFF000000)
val EvPrimaryContainer = Color(0xFF422C00)
val EvOnPrimaryContainer = Color(0xFFFFDFA0)

val EvSecondary = PlexAmberGlow
val EvOnSecondary = Color(0xFF2A1B00)
val EvSecondaryContainer = Color(0xFF3D2E14)
val EvOnSecondaryContainer = Color(0xFFFFE8B3)

val EvTertiary = PlexGreen
val EvOnTertiary = Color(0xFF003919)
val EvTertiaryContainer = Color(0xFF005327)
val EvOnTertiaryContainer = Color(0xFF6BFF9A)

val EvBackground = PlexBackground
val EvOnBackground = PlexTextPrimary
val EvSurface = PlexSurface
val EvOnSurface = PlexTextPrimary
val EvSurfaceVariant = PlexCard
val EvOnSurfaceVariant = PlexTextSecondary
