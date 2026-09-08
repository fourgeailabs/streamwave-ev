package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.ServerEntity
import com.example.data.model.ServerType
import com.example.data.model.TrackEntity
import com.example.data.remote.PlexAuthService
import com.example.data.remote.PlexPinState
import com.example.data.remote.PlexServerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class StreamWaveRepository(
    private val context: Context,
    private val database: AppDatabase = AppDatabase.getInstance(context),
    private val plexService: PlexAuthService = PlexAuthService(context)
) {
    private val trackDao = database.trackDao()
    private val serverDao = database.serverDao()

    val allTracks: Flow<List<TrackEntity>> = trackDao.getAllTracks()
    val downloadedTracks: Flow<List<TrackEntity>> = trackDao.getDownloadedTracks()
    val favoriteTracks: Flow<List<TrackEntity>> = trackDao.getFavoriteTracks()
    val allServers: Flow<List<ServerEntity>> = serverDao.getAllServers()
    val defaultServer: Flow<ServerEntity?> = serverDao.getDefaultServer()
    val downloadedCount: Flow<Int> = trackDao.getDownloadedCount()

    fun searchTracks(query: String): Flow<List<TrackEntity>> = trackDao.searchTracks(query)

    suspend fun getTrackById(id: Long): TrackEntity? = trackDao.getTrackById(id)

    suspend fun toggleFavorite(trackId: Long, isFavorite: Boolean) {
        trackDao.setFavorite(trackId, isFavorite)
    }

    suspend fun toggleDownload(trackId: Long, currentDownloaded: Boolean) = withContext(Dispatchers.IO) {
        val track = trackDao.getTrackById(trackId) ?: return@withContext
        if (currentDownloaded) {
            // Remove local file
            track.localFilePath?.let { path ->
                val file = File(path)
                if (file.exists()) file.delete()
            }
            trackDao.setDownloaded(trackId, false, null)
        } else {
            // Save to offline storage cache
            val cacheDir = File(context.filesDir, "audio_cache")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val localFile = File(cacheDir, "track_${track.id}.mp3")
            if (!localFile.exists()) {
                localFile.writeBytes("StreamWave Offline Cached Audio Track ${track.title}".toByteArray())
            }
            trackDao.setDownloaded(trackId, true, localFile.absolutePath)
        }
    }

    suspend fun recordPlay(trackId: Long) {
        trackDao.incrementPlayCount(trackId)
    }

    suspend fun addServer(server: ServerEntity): Long {
        return serverDao.insertServer(server)
    }

    suspend fun setDefaultServer(serverId: Long) {
        serverDao.clearDefaultServer()
        serverDao.setDefaultServer(serverId)
    }

    suspend fun deleteServer(serverId: Long) {
        serverDao.deleteServer(serverId)
    }

    suspend fun refreshServerPings() = withContext(Dispatchers.IO) {
        val simulatedPings = mapOf(1L to 18L, 2L to 32L, 3L to 45L)
        simulatedPings.forEach { (id, ping) ->
            serverDao.updatePingStatus(id, true, ping)
        }
    }

    // Plex TV PIN Operations
    suspend fun requestPlexPin(): PlexPinState = plexService.requestPin()

    suspend fun checkPlexPin(pinId: Long, code: String): PlexPinState = plexService.checkPinStatus(pinId, code)

    suspend fun discoverPlexServers(token: String): List<PlexServerInfo> = plexService.discoverServers(token)

    suspend fun verifyDirectPlexServer(serverUrl: String, token: String): Boolean =
        plexService.verifyDirectServer(serverUrl, token)

    suspend fun initializeDefaultDataIfEmpty() = withContext(Dispatchers.IO) {
        if (trackDao.getTrackCount() == 0) {
            val defaultPlex = ServerEntity(
                name = "Plex Media Server (Home)",
                type = ServerType.PLEX,
                serverUrl = "https://plex.home.lan:32400",
                authToken = "plex_ev_token_sample",
                isOnline = true,
                lastPingMs = 14,
                isDefault = true
            )
            val subsonicServer = ServerEntity(
                name = "Navidrome EV Flac Server",
                type = ServerType.SUBSONIC,
                serverUrl = "https://music.home.lan:4533",
                username = "driver",
                isOnline = true,
                lastPingMs = 28,
                isDefault = false
            )
            val jellyfinServer = ServerEntity(
                name = "Jellyfin Studio Hi-Res",
                type = ServerType.JELLYFIN,
                serverUrl = "https://jellyfin.local:8096",
                isOnline = true,
                lastPingMs = 36,
                isDefault = false
            )

            val plexId = serverDao.insertServer(defaultPlex)
            serverDao.insertServer(subsonicServer)
            serverDao.insertServer(jellyfinServer)

            val initialTracks = listOf(
                TrackEntity(
                    serverId = plexId,
                    title = "Electric Horizon (Equinox Theme)",
                    artist = "Neon Highway",
                    album = "Silent Velocity",
                    durationMs = 214000,
                    streamUrl = "https://actions.google.com/sounds/v1/ambient/rain_heavy.ogg",
                    albumArtUrl = "https://picsum.photos/seed/equinox1/500/500",
                    lrcLyrics = """
[00:00.00] (Synth pulse intro - 120 BPM)
[00:06.50] Gliding through the quiet night
[00:12.80] Miles of road and dashboard light
[00:19.40] Instant torque and zero sound
[00:25.70] Wheels in motion off the ground
[00:32.50] Electric horizon, leading the way
[00:38.80] Never looking back to yesterday
[00:45.30] Charged and ready for the midnight run
[00:52.00] Riding straight into the morning sun
[01:04.50] StreamWave pulsing through the car
[01:11.20] Guided by a distant star
[01:17.80] Bass is deep and notes are clear
[01:24.40] Best acoustic sound is here
[01:31.00] Electric horizon, leading the way
[01:37.50] Never looking back to yesterday
[01:44.20] Charged and ready for the midnight run
[01:50.80] Riding straight into the morning sun
                    """.trimIndent(),
                    isDownloaded = true,
                    isFavorite = true,
                    playCount = 12
                ),
                TrackEntity(
                    serverId = plexId,
                    title = "Midnight Cruise (Subwoofer Mix)",
                    artist = "Pulse Resonance",
                    album = "EV Soundstage",
                    durationMs = 195000,
                    streamUrl = "https://actions.google.com/sounds/v1/weather/wind_arctic_constant.ogg",
                    albumArtUrl = "https://picsum.photos/seed/equinox2/500/500",
                    lrcLyrics = """
[00:00.00] (Deep bass vibrations activate)
[00:08.20] Subwoofers humming underneath the floor
[00:15.50] Open highway through the cabin door
[00:22.40] Digital displays glowing crystal blue
[00:29.80] Streaming home audio crystal true
[00:37.00] Hands upon the wheel, eyes upon the turn
[00:44.20] No emissions and no fuel to burn
[00:51.50] Turn the volume higher, let the rhythm ride
[00:58.70] Seamless entertainment on the driver's side
[01:12.00] Clear acoustic stage, surround sound pure
[01:19.40] Everything is steady, everything secure
                    """.trimIndent(),
                    isDownloaded = true,
                    isFavorite = true,
                    playCount = 8
                ),
                TrackEntity(
                    serverId = plexId,
                    title = "Cybernetic Sunrise",
                    artist = "Aetherial",
                    album = "Digital Dreamscape",
                    durationMs = 248000,
                    streamUrl = "https://actions.google.com/sounds/v1/water/waves_crashing_on_rock_beach.ogg",
                    albumArtUrl = "https://picsum.photos/seed/equinox3/500/500",
                    lrcLyrics = """
[00:00.00] (Atmospheric ambient chords)
[00:10.00] Golden rays pierce through the mist
[00:18.50] Morning drive that can't be missed
[00:26.20] Whispering wind across the glass
[00:34.00] Watching silver reflections pass
[00:42.50] High fidelity in every tone
[00:50.20] The road ahead is all our own
[00:58.80] Synchronized lyrics singing bright
[01:07.40] Escaping into warmth and light
                    """.trimIndent(),
                    isDownloaded = false,
                    isFavorite = false,
                    playCount = 4
                ),
                TrackEntity(
                    serverId = plexId,
                    title = "Battery Full (Fast Lane)",
                    artist = "Volt Surge",
                    album = "High Voltage",
                    durationMs = 182000,
                    streamUrl = "https://actions.google.com/sounds/v1/science/scifi_laser_engine_hum.ogg",
                    albumArtUrl = "https://picsum.photos/seed/equinox4/500/500",
                    lrcLyrics = """
[00:00.00] (Fast arpeggio synthesizer)
[00:05.40] Hundred percent on the gauge today
[00:11.20] Five hundred miles to make our way
[00:17.10] Hit the pedal feel the surge of power
[00:23.00] Sixty seconds to the finest hour
[00:29.10] Voice command says play my favorite track
[00:35.00] We're on our way and we won't turn back
[00:41.00] Fast lane cruising with the stereo loud
[00:47.20] Rising above every storm and cloud
                    """.trimIndent(),
                    isDownloaded = false,
                    isFavorite = true,
                    playCount = 19
                ),
                TrackEntity(
                    serverId = plexId,
                    title = "Lossless Reverie",
                    artist = "Acoustic Flow",
                    album = "Studio Master Tape",
                    durationMs = 230000,
                    streamUrl = "https://actions.google.com/sounds/v1/weather/thunder_crack.ogg",
                    albumArtUrl = "https://picsum.photos/seed/equinox5/500/500",
                    lrcLyrics = """
[00:00.00] (Warm acoustic instruments)
[00:09.50] Hear the fingers sliding on the steel
[00:18.00] Every harmonic frequency you feel
[00:26.80] From home servers through the wireless air
[00:35.20] Pure uncompressed beauty everywhere
[00:44.00] Equinox cabin becomes a concert hall
[00:52.50] Music flowing softly through it all
                    """.trimIndent(),
                    isDownloaded = true,
                    isFavorite = false,
                    playCount = 6
                )
            )

            trackDao.insertTracks(initialTracks)
        }
    }
}
