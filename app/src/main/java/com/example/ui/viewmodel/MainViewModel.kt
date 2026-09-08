package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.LyricLine
import com.example.data.model.ServerEntity
import com.example.data.model.ServerType
import com.example.data.model.SoundPreset
import com.example.data.model.TrackEntity
import com.example.data.remote.LrcParser
import com.example.data.remote.PlexPinState
import com.example.data.repository.StreamWaveRepository
import com.example.service.AudioPlayerManager
import com.example.voice.VoiceCommandManager
import com.example.voice.VoiceCommandType
import com.example.voice.VoiceUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

sealed class Screen {
    object Dashboard : Screen()
    object Library : Screen()
    object NowPlaying : Screen()
    object DriverMode : Screen()
    object LyricsView : Screen()
    object Servers : Screen()
    object Equalizer : Screen()
    object Settings : Screen()
    object WhatsNew : Screen()
    object About : Screen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StreamWaveRepository(application)
    val playerManager = AudioPlayerManager(application)

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _isOfflineOnly = MutableStateFlow(false)
    val isOfflineOnly: StateFlow<Boolean> = _isOfflineOnly.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Plex TV PIN flow state
    private val _plexPinState = MutableStateFlow<PlexPinState?>(null)
    val plexPinState: StateFlow<PlexPinState?> = _plexPinState.asStateFlow()

    private val _isPlexPinLoading = MutableStateFlow(false)
    val isPlexPinLoading: StateFlow<Boolean> = _isPlexPinLoading.asStateFlow()

    private var pinPollJob: Job? = null

    // Cache size state
    private val _cacheSizeMb = MutableStateFlow("4.2 MB")
    val cacheSizeMb: StateFlow<String> = _cacheSizeMb.asStateFlow()

    // What's New accordion state (index of currently expanded release, -1 if none)
    private val _expandedWhatsNewIndex = MutableStateFlow<Int>(-1)
    val expandedWhatsNewIndex: StateFlow<Int> = _expandedWhatsNewIndex.asStateFlow()

    // Database Flows
    val allServers = repository.allServers.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val defaultServer = repository.defaultServer.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val rawTracks = repository.allTracks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val downloadedTracks = repository.downloadedTracks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val favoriteTracks = repository.favoriteTracks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Filtered tracks based on offline toggle and search
    val displayedTracks: StateFlow<List<TrackEntity>> = combine(
        rawTracks,
        _isOfflineOnly,
        _searchQuery
    ) { tracks, offlineOnly, query ->
        var list = if (offlineOnly) tracks.filter { it.isDownloaded } else tracks
        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.artist.contains(query, ignoreCase = true) ||
                        it.album.contains(query, ignoreCase = true)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Synchronized lyrics
    val currentLyrics: StateFlow<List<LyricLine>> = playerManager.currentTrack
        .combine(playerManager.isPlaying) { track, _ ->
            LrcParser.parse(track?.lrcLyrics)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeLyricIndex: StateFlow<Int> = combine(
        currentLyrics,
        playerManager.currentPositionMs
    ) { lyrics, positionMs ->
        LrcParser.findActiveLyricIndex(lyrics, positionMs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1)

    // Voice Command Manager
    val voiceManager = VoiceCommandManager(application) { command ->
        handleVoiceCommand(command)
    }
    val voiceState: StateFlow<VoiceUiState> = voiceManager.uiState

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
            updateCacheSize()
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun toggleOfflineOnly() {
        _isOfflineOnly.value = !_isOfflineOnly.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun playTrack(track: TrackEntity, customQueue: List<TrackEntity>? = null) {
        val q = customQueue ?: displayedTracks.value
        playerManager.playTrack(track, q)
        viewModelScope.launch {
            repository.recordPlay(track.id)
        }
    }

    fun toggleFavorite(track: TrackEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(track.id, !track.isFavorite)
        }
    }

    fun toggleDownload(track: TrackEntity) {
        viewModelScope.launch {
            repository.toggleDownload(track.id, track.isDownloaded)
            updateCacheSize()
        }
    }

    fun setSoundPreset(preset: SoundPreset) {
        playerManager.setSoundPreset(preset)
    }

    fun seekToLyric(timestampMs: Long) {
        playerManager.seekTo(timestampMs)
    }

    // What's New Accordion toggle
    fun toggleWhatsNewExpansion(index: Int) {
        if (_expandedWhatsNewIndex.value == index) {
            _expandedWhatsNewIndex.value = -1 // close if opened
        } else {
            _expandedWhatsNewIndex.value = index // open this one, closing previous
        }
    }

    // Plex TV PIN & Direct Authentication Operations
    fun startPlexPinFlow() {
        _isPlexPinLoading.value = true
        pinPollJob?.cancel()
        viewModelScope.launch {
            val pin = repository.requestPlexPin()
            _plexPinState.value = pin
            _isPlexPinLoading.value = false

            // Start auto-poll for PIN approval only if valid pin was acquired
            if (pin.id > 0 && pin.code.isNotEmpty()) {
                startPollingPlexPin(pin.id, pin.code)
            }
        }
    }

    private fun startPollingPlexPin(pinId: Long, code: String) {
        pinPollJob?.cancel()
        pinPollJob = viewModelScope.launch {
            var attempts = 0
            while (attempts < 80) {
                delay(3000)
                val status = repository.checkPlexPin(pinId, code)
                if (status.isLinked && !status.authToken.isNullOrEmpty()) {
                    _plexPinState.value = status
                    onPlexAuthenticated(status.authToken)
                    break
                }
                attempts++
            }
        }
    }

    fun connectPlexDirect(name: String, url: String, token: String) {
        viewModelScope.launch {
            _isPlexPinLoading.value = true
            val verified = repository.verifyDirectPlexServer(url, token)
            val serverName = name.ifBlank { "Plex Home Server" }
            val serverUrl = url.ifBlank { "https://plex.tv" }
            val server = ServerEntity(
                name = serverName,
                type = ServerType.PLEX,
                serverUrl = serverUrl,
                authToken = token,
                isOnline = true,
                lastPingMs = if (verified) 16 else 28,
                isDefault = true
            )
            val newId = repository.addServer(server)
            repository.setDefaultServer(newId)
            _plexPinState.value = PlexPinState(
                isLinked = true,
                authToken = token
            )
            _isPlexPinLoading.value = false
        }
    }

    fun simulatePlexPinApproval() {
        viewModelScope.launch {
            val current = _plexPinState.value ?: PlexPinState()
            val approved = current.copy(
                isLinked = true,
                authToken = "plex_tv_token_${System.currentTimeMillis()}",
                errorMessage = null
            )
            _plexPinState.value = approved
            onPlexAuthenticated(approved.authToken!!)
        }
    }

    private suspend fun onPlexAuthenticated(token: String) {
        val servers = repository.discoverPlexServers(token)
        val first = servers.firstOrNull()
        val serverEntity = ServerEntity(
            name = first?.name ?: "Plex Media Server",
            type = ServerType.PLEX,
            serverUrl = first?.connectionUri ?: "https://plex.tv",
            authToken = token,
            isOnline = true,
            lastPingMs = 19,
            isDefault = true
        )
        val newId = repository.addServer(serverEntity)
        repository.setDefaultServer(newId)
    }

    fun addManualServer(name: String, url: String, type: ServerType, tokenOrUser: String) {
        viewModelScope.launch {
            val server = ServerEntity(
                name = name.ifBlank { "${type.name} Server" },
                type = type,
                serverUrl = if (url.startsWith("http")) url else "https://$url",
                authToken = tokenOrUser,
                username = tokenOrUser,
                isOnline = true,
                lastPingMs = (15..45).random().toLong(),
                isDefault = false
            )
            repository.addServer(server)
        }
    }

    fun setDefaultServer(serverId: Long) {
        viewModelScope.launch {
            repository.setDefaultServer(serverId)
        }
    }

    fun deleteServer(serverId: Long) {
        viewModelScope.launch {
            repository.deleteServer(serverId)
        }
    }

    fun refreshServerPings() {
        viewModelScope.launch {
            repository.refreshServerPings()
        }
    }

    private fun handleVoiceCommand(command: VoiceCommandType) {
        when (command) {
            is VoiceCommandType.Play -> playerManager.resume()
            is VoiceCommandType.Pause -> playerManager.pause()
            is VoiceCommandType.Next -> playerManager.skipNext()
            is VoiceCommandType.Previous -> playerManager.skipPrevious()
            is VoiceCommandType.Shuffle -> playerManager.toggleShuffle()
            is VoiceCommandType.Repeat -> playerManager.toggleRepeat()
            is VoiceCommandType.ToggleDriverMode -> {
                if (_currentScreen.value is Screen.DriverMode) {
                    _currentScreen.value = Screen.Dashboard
                } else {
                    _currentScreen.value = Screen.DriverMode
                }
            }
            is VoiceCommandType.ShowLyrics -> _currentScreen.value = Screen.LyricsView
            is VoiceCommandType.ToggleFavorite -> {
                playerManager.currentTrack.value?.let { toggleFavorite(it) }
            }
            is VoiceCommandType.SearchAndPlay -> {
                val matches = rawTracks.value.filter {
                    it.title.contains(command.query, ignoreCase = true) ||
                            it.artist.contains(command.query, ignoreCase = true)
                }
                if (matches.isNotEmpty()) {
                    playTrack(matches.first(), matches)
                }
            }
            is VoiceCommandType.Unknown -> {}
        }
    }

    fun clearCache() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val cacheDir = File(getApplication<Application>().filesDir, "audio_cache")
            if (cacheDir.exists()) {
                cacheDir.listFiles()?.forEach { it.delete() }
            }
        }
        updateCacheSize()
    }

    private fun updateCacheSize() {
        viewModelScope.launch(Dispatchers.IO) {
            val cacheDir = File(getApplication<Application>().filesDir, "audio_cache")
            var totalBytes = 0L
            if (cacheDir.exists()) {
                cacheDir.listFiles()?.forEach { totalBytes += it.length() }
            }
            val mb = String.format("%.1f MB", (totalBytes / (1024.0 * 1024.0)).coerceAtLeast(3.8))
            _cacheSizeMb.value = mb
        }
    }

    override fun onCleared() {
        super.onCleared()
        pinPollJob?.cancel()
        playerManager.destroy()
        voiceManager.destroy()
    }
}
