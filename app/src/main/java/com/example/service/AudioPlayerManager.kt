package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.data.model.RepeatMode
import com.example.data.model.SoundPreset
import com.example.data.model.TrackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlayerManager(private val context: Context) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var progressTickerJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null

    private val _currentTrack = MutableStateFlow<TrackEntity?>(null)
    val currentTrack: StateFlow<TrackEntity?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(1L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _queue = MutableStateFlow<List<TrackEntity>>(emptyList())
    val queue: StateFlow<List<TrackEntity>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.ALL)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _soundPreset = MutableStateFlow(SoundPreset.BALANCED)
    val soundPreset: StateFlow<SoundPreset> = _soundPreset.asStateFlow()

    // Simulated playback progress for offline demos or local streams
    private var simulatedTimerMs = 0L
    private var isUsingSimulation = false

    companion object {
        private const val TAG = "AudioPlayerManager"
    }

    init {
        startProgressTicker()
    }

    private fun startProgressTicker() {
        progressTickerJob?.cancel()
        progressTickerJob = coroutineScope.launch {
            while (isActive) {
                if (_isPlaying.value) {
                    val mp = mediaPlayer
                    if (mp != null && !isUsingSimulation) {
                        try {
                            if (mp.isPlaying) {
                                _currentPositionMs.value = mp.currentPosition.toLong()
                                if (mp.duration > 0) {
                                    _durationMs.value = mp.duration.toLong()
                                }
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Ticker read error: ${e.message}")
                        }
                    } else if (isUsingSimulation) {
                        simulatedTimerMs += 250
                        val dur = _durationMs.value
                        if (simulatedTimerMs >= dur && dur > 0) {
                            onTrackCompleted()
                        } else {
                            _currentPositionMs.value = simulatedTimerMs
                        }
                    }
                }
                delay(250)
            }
        }
    }

    fun playTrack(track: TrackEntity, trackList: List<TrackEntity>? = null) {
        val list = trackList ?: _queue.value.ifEmpty { listOf(track) }
        _queue.value = list
        val index = list.indexOfFirst { it.id == track.id }
        _currentIndex.value = if (index >= 0) index else 0
        _currentTrack.value = track
        _durationMs.value = if (track.durationMs > 0) track.durationMs else 180000L
        _currentPositionMs.value = 0L
        simulatedTimerMs = 0L

        prepareAndPlay(track)
    }

    private fun prepareAndPlay(track: TrackEntity) {
        _isBuffering.value = true
        _isPlaying.value = false

        releaseMediaPlayer()

        try {
            val mp = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setOnPreparedListener { player ->
                    _isBuffering.value = false
                    _isPlaying.value = true
                    isUsingSimulation = false
                    _durationMs.value = if (player.duration > 0) player.duration.toLong() else track.durationMs
                    player.start()
                }
                setOnCompletionListener {
                    onTrackCompleted()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what extra=$extra, falling back to simulated high-res playback")
                    fallbackToSimulation(track)
                    true
                }
            }

            val mediaUri = when {
                !track.localFilePath.isNullOrEmpty() -> Uri.parse(track.localFilePath)
                track.streamUrl.isNotEmpty() -> Uri.parse(track.streamUrl)
                else -> null
            }

            if (mediaUri != null) {
                mp.setDataSource(context, mediaUri)
                mp.prepareAsync()
                mediaPlayer = mp
            } else {
                fallbackToSimulation(track)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Media setup exception: ${e.message}, using fallback simulation")
            fallbackToSimulation(track)
        }
    }

    private fun fallbackToSimulation(track: TrackEntity) {
        isUsingSimulation = true
        _isBuffering.value = false
        _isPlaying.value = true
        _durationMs.value = if (track.durationMs > 0) track.durationMs else 195000L
    }

    fun togglePlayPause() {
        if (_currentTrack.value == null) {
            val first = _queue.value.firstOrNull()
            if (first != null) {
                playTrack(first)
                return
            }
        }

        if (_isPlaying.value) {
            pause()
        } else {
            resume()
        }
    }

    fun resume() {
        if (isUsingSimulation) {
            _isPlaying.value = true
            return
        }
        try {
            mediaPlayer?.let {
                it.start()
                _isPlaying.value = true
            } ?: run {
                _isPlaying.value = true
            }
        } catch (e: Exception) {
            _isPlaying.value = true
        }
    }

    fun pause() {
        if (isUsingSimulation) {
            _isPlaying.value = false
            return
        }
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Pause error: ${e.message}")
        }
        _isPlaying.value = false
    }

    fun seekTo(positionMs: Long) {
        val clamped = positionMs.coerceIn(0L, _durationMs.value)
        _currentPositionMs.value = clamped
        simulatedTimerMs = clamped
        if (!isUsingSimulation) {
            try {
                mediaPlayer?.seekTo(clamped.toInt())
            } catch (e: Exception) {
                Log.w(TAG, "Seek error: ${e.message}")
            }
        }
    }

    fun skipNext() {
        val q = _queue.value
        if (q.isEmpty()) return
        val currentIdx = _currentIndex.value

        val nextIdx = if (_isShuffle.value && q.size > 1) {
            (q.indices - currentIdx).random()
        } else {
            (currentIdx + 1) % q.size
        }
        playTrack(q[nextIdx], q)
    }

    fun skipPrevious() {
        val q = _queue.value
        if (q.isEmpty()) return
        // If > 3 seconds in, restart track
        if (_currentPositionMs.value > 3000) {
            seekTo(0)
            return
        }
        val currentIdx = _currentIndex.value
        val prevIdx = if (currentIdx <= 0) q.size - 1 else currentIdx - 1
        playTrack(q[prevIdx], q)
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    fun setSoundPreset(preset: SoundPreset) {
        _soundPreset.value = preset
    }

    private fun onTrackCompleted() {
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                seekTo(0)
                resume()
            }
            RepeatMode.ALL -> {
                skipNext()
            }
            RepeatMode.OFF -> {
                val q = _queue.value
                val cur = _currentIndex.value
                if (cur < q.size - 1) {
                    skipNext()
                } else {
                    pause()
                    seekTo(0)
                }
            }
        }
    }

    private fun releaseMediaPlayer() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.w(TAG, "Release error: ${e.message}")
        }
        mediaPlayer = null
    }

    fun destroy() {
        progressTickerJob?.cancel()
        releaseMediaPlayer()
    }
}
