package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ServerType {
    PLEX,
    SUBSONIC,
    JELLYFIN,
    LOCAL_CACHE
}

@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: ServerType,
    val serverUrl: String,
    val authToken: String = "",
    val username: String = "",
    val clientIdentifier: String = "",
    val isOnline: Boolean = true,
    val lastPingMs: Long = 24,
    val isDefault: Boolean = false
)

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serverId: Long = 0,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val streamUrl: String,
    val localFilePath: String? = null,
    val albumArtUrl: String? = null,
    val lrcLyrics: String? = null,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val addedTimestamp: Long = System.currentTimeMillis()
)

data class LyricLine(
    val timestampMs: Long,
    val text: String
)

enum class PlaybackMode {
    NORMAL,
    SHUFFLE
}

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

enum class SoundPreset(val displayName: String, val description: String) {
    BALANCED("Equinox Balanced", "Neutral frequency response for cabin acoustics"),
    DRIVER_CENTRIC("Driver Focus", "Optimized soundstage directed toward driver seat"),
    REAR_PASSENGERS("Cabin Wide", "Enhanced volume and spatialization across all rows"),
    EV_DEEP_BASS("EV Deep Bass", "Low-end punch tuned for quiet electric cabin"),
    VOCAL_CLARITY("Vocal & Podcast", "High speech intelligibility and crisp treble")
}
