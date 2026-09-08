package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.DriverModeView
import com.example.ui.components.EquinoxEqualizerView
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.PlexPinDialog
import com.example.ui.components.SyncedLyricsViewer
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.NowPlayingScreen
import com.example.ui.screens.ServerManagementScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WhatsNewScreen
import com.example.ui.theme.EvBackground
import com.example.ui.theme.EvBorder
import com.example.ui.theme.EvCardElevated
import com.example.ui.theme.EvCardSurface
import com.example.ui.theme.EvCyan
import com.example.ui.theme.EvDarkNavy
import com.example.ui.theme.EvNeonGreen
import com.example.ui.theme.EvTextPrimary
import com.example.ui.theme.EvTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen
import com.example.voice.VoiceUiState

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                StreamWaveApp(viewModel = viewModel, activity = this)
            }
        }
    }
}

@Composable
fun StreamWaveApp(
    viewModel: MainViewModel,
    activity: ComponentActivity
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentTrack by viewModel.playerManager.currentTrack.collectAsState()
    val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
    val positionMs by viewModel.playerManager.currentPositionMs.collectAsState()
    val durationMs by viewModel.playerManager.durationMs.collectAsState()
    val isShuffle by viewModel.playerManager.isShuffle.collectAsState()
    val repeatMode by viewModel.playerManager.repeatMode.collectAsState()
    val soundPreset by viewModel.playerManager.soundPreset.collectAsState()

    val displayedTracks by viewModel.displayedTracks.collectAsState()
    val isOfflineOnly by viewModel.isOfflineOnly.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val defaultServer by viewModel.defaultServer.collectAsState()
    val allServers by viewModel.allServers.collectAsState()

    val lyrics by viewModel.currentLyrics.collectAsState()
    val activeLyricIndex by viewModel.activeLyricIndex.collectAsState()

    val voiceState by viewModel.voiceState.collectAsState()

    val plexPinState by viewModel.plexPinState.collectAsState()
    val isPlexLoading by viewModel.isPlexPinLoading.collectAsState()
    var showPlexDialog by remember { mutableStateOf(false) }

    val cacheSize by viewModel.cacheSizeMb.collectAsState()
    val expandedWhatsNew by viewModel.expandedWhatsNewIndex.collectAsState()

    // Audio Permission Launcher for Voice Commands
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.voiceManager.startListening()
        }
    }

    val requestVoiceStart = {
        val hasPermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.voiceManager.startListening()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Driver Mode Full Screen HUD Takeover
    if (currentScreen is Screen.DriverMode) {
        DriverModeView(
            currentTrack = currentTrack,
            isPlaying = isPlaying,
            positionMs = positionMs,
            durationMs = durationMs,
            onPlayPause = {
                if (isPlaying) viewModel.playerManager.pause() else viewModel.playerManager.resume()
            },
            onNext = { viewModel.playerManager.skipNext() },
            onPrevious = { viewModel.playerManager.skipPrevious() },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onStartVoice = requestVoiceStart,
            onExitDriverMode = { viewModel.navigateTo(Screen.Dashboard) }
        )
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(EvBackground)) {
        val isWideScreen = maxWidth >= 720.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // Adaptive Navigation: Rail for EV Screens & Tablets; Bottom Bar for Phones
            if (isWideScreen && currentScreen !is Screen.NowPlaying) {
                NavigationRail(
                    containerColor = EvDarkNavy,
                    contentColor = EvCyan,
                    modifier = Modifier.border(width = 1.dp, color = EvBorder)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(EvCyan.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Equinox EV",
                            tint = EvCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    val navRailColors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = EvCyan,
                        indicatorColor = EvCyan,
                        unselectedIconColor = EvTextSecondary,
                        unselectedTextColor = EvTextSecondary
                    )

                    NavigationRailItem(
                        selected = currentScreen is Screen.Dashboard,
                        onClick = { viewModel.navigateTo(Screen.Dashboard) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Cockpit") },
                        colors = navRailColors,
                        modifier = Modifier.testTag("rail_cockpit")
                    )

                    NavigationRailItem(
                        selected = currentScreen is Screen.Library,
                        onClick = { viewModel.navigateTo(Screen.Library) },
                        icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                        label = { Text("Library") },
                        colors = navRailColors,
                        modifier = Modifier.testTag("rail_library")
                    )

                    NavigationRailItem(
                        selected = currentScreen is Screen.Servers,
                        onClick = { viewModel.navigateTo(Screen.Servers) },
                        icon = { Icon(Icons.Default.Dns, contentDescription = "Servers") },
                        label = { Text("Servers") },
                        colors = navRailColors,
                        modifier = Modifier.testTag("rail_servers")
                    )

                    NavigationRailItem(
                        selected = currentScreen is Screen.Equalizer,
                        onClick = { viewModel.navigateTo(Screen.Equalizer) },
                        icon = { Icon(Icons.Default.Equalizer, contentDescription = "Equalizer") },
                        label = { Text("Acoustics") },
                        colors = navRailColors,
                        modifier = Modifier.testTag("rail_equalizer")
                    )

                    NavigationRailItem(
                        selected = currentScreen is Screen.Settings,
                        onClick = { viewModel.navigateTo(Screen.Settings) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        colors = navRailColors,
                        modifier = Modifier.testTag("rail_settings")
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Quick Driver Mode Button in Rail
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.DriverMode) },
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(EvCyan)
                            .testTag("rail_driver_mode_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Driver Mode",
                            tint = Color(0xFF021727),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Main Content Body
            Scaffold(
                bottomBar = {
                    Column {
                        // Persistent Mini Player
                        if (currentTrack != null && currentScreen !is Screen.NowPlaying && currentScreen !is Screen.DriverMode) {
                            MiniPlayerBar(
                                currentTrack = currentTrack,
                                isPlaying = isPlaying,
                                positionMs = positionMs,
                                durationMs = durationMs,
                                onBarClick = { viewModel.navigateTo(Screen.NowPlaying) },
                                onPlayPause = {
                                    if (isPlaying) viewModel.playerManager.pause() else viewModel.playerManager.resume()
                                },
                                onNext = { viewModel.playerManager.skipNext() },
                                onPrevious = { viewModel.playerManager.skipPrevious() },
                                onOpenLyrics = { viewModel.navigateTo(Screen.LyricsView) },
                                onVoiceClick = requestVoiceStart
                            )
                        }

                        // Bottom Navigation Bar for Compact / Phones
                        if (!isWideScreen && currentScreen !is Screen.NowPlaying && currentScreen !is Screen.LyricsView) {
                            NavigationBar(
                                containerColor = EvDarkNavy,
                                contentColor = EvCyan,
                                modifier = Modifier.border(width = 1.dp, color = EvBorder)
                            ) {
                                val navBarColors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = EvCyan,
                                    indicatorColor = EvCyan,
                                    unselectedIconColor = EvTextSecondary,
                                    unselectedTextColor = EvTextSecondary
                                )

                                NavigationBarItem(
                                    selected = currentScreen is Screen.Dashboard,
                                    onClick = { viewModel.navigateTo(Screen.Dashboard) },
                                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                                    label = { Text("Cockpit") },
                                    colors = navBarColors,
                                    modifier = Modifier.testTag("bottom_cockpit")
                                )

                                NavigationBarItem(
                                    selected = currentScreen is Screen.Library,
                                    onClick = { viewModel.navigateTo(Screen.Library) },
                                    icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                                    label = { Text("Library") },
                                    colors = navBarColors,
                                    modifier = Modifier.testTag("bottom_library")
                                )

                                NavigationBarItem(
                                    selected = currentScreen is Screen.Servers,
                                    onClick = { viewModel.navigateTo(Screen.Servers) },
                                    icon = { Icon(Icons.Default.Dns, contentDescription = "Servers") },
                                    label = { Text("Servers") },
                                    colors = navBarColors,
                                    modifier = Modifier.testTag("bottom_servers")
                                )

                                NavigationBarItem(
                                    selected = currentScreen is Screen.Equalizer,
                                    onClick = { viewModel.navigateTo(Screen.Equalizer) },
                                    icon = { Icon(Icons.Default.Equalizer, contentDescription = "Equalizer") },
                                    label = { Text("Acoustics") },
                                    colors = navBarColors,
                                    modifier = Modifier.testTag("bottom_equalizer")
                                )

                                NavigationBarItem(
                                    selected = currentScreen is Screen.Settings,
                                    onClick = { viewModel.navigateTo(Screen.Settings) },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                    label = { Text("Settings") },
                                    colors = navBarColors,
                                    modifier = Modifier.testTag("bottom_settings")
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        is Screen.Dashboard -> {
                            DashboardScreen(
                                activeServer = defaultServer,
                                tracks = displayedTracks,
                                currentTrack = currentTrack,
                                onPlayTrack = { viewModel.playTrack(it) },
                                onStartVoice = requestVoiceStart,
                                onOpenDriverMode = { viewModel.navigateTo(Screen.DriverMode) },
                                onOpenPlexPin = {
                                    viewModel.startPlexPinFlow()
                                    showPlexDialog = true
                                },
                                onOpenServers = { viewModel.navigateTo(Screen.Servers) },
                                onOpenEqualizer = { viewModel.navigateTo(Screen.Equalizer) }
                            )
                        }

                        is Screen.Library -> {
                            LibraryScreen(
                                tracks = displayedTracks,
                                currentTrack = currentTrack,
                                isOfflineOnly = isOfflineOnly,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                onToggleOfflineOnly = { viewModel.toggleOfflineOnly() },
                                onPlayTrack = { viewModel.playTrack(it) },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onToggleDownload = { viewModel.toggleDownload(it) }
                            )
                        }

                        is Screen.NowPlaying -> {
                            NowPlayingScreen(
                                currentTrack = currentTrack,
                                isPlaying = isPlaying,
                                positionMs = positionMs,
                                durationMs = durationMs,
                                isShuffle = isShuffle,
                                repeatMode = repeatMode,
                                soundPreset = soundPreset,
                                lyrics = lyrics,
                                activeLyricIndex = activeLyricIndex,
                                onPlayPause = {
                                    if (isPlaying) viewModel.playerManager.pause() else viewModel.playerManager.resume()
                                },
                                onNext = { viewModel.playerManager.skipNext() },
                                onPrevious = { viewModel.playerManager.skipPrevious() },
                                onSeekTo = { viewModel.playerManager.seekTo(it) },
                                onToggleShuffle = { viewModel.playerManager.toggleShuffle() },
                                onToggleRepeat = { viewModel.playerManager.toggleRepeat() },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onToggleDownload = { viewModel.toggleDownload(it) },
                                onOpenEqualizer = { viewModel.navigateTo(Screen.Equalizer) },
                                onOpenDriverMode = { viewModel.navigateTo(Screen.DriverMode) },
                                onStartVoice = requestVoiceStart,
                                onBack = { viewModel.navigateTo(Screen.Dashboard) }
                            )
                        }

                        is Screen.LyricsView -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                SyncedLyricsViewer(
                                    lyrics = lyrics,
                                    activeLineIndex = activeLyricIndex,
                                    onLineClick = { viewModel.seekToLyric(it) }
                                )

                                IconButton(
                                    onClick = { viewModel.navigateTo(Screen.NowPlaying) },
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.TopStart)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E293B))
                                        .testTag("lyrics_back_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = EvCyan
                                    )
                                }
                            }
                        }

                        is Screen.Servers -> {
                            ServerManagementScreen(
                                servers = allServers,
                                onOpenPlexPinDialog = {
                                    viewModel.startPlexPinFlow()
                                    showPlexDialog = true
                                },
                                onAddManualServer = { name, url, type, auth ->
                                    viewModel.addManualServer(name, url, type, auth)
                                },
                                onSetDefaultServer = { viewModel.setDefaultServer(it) },
                                onDeleteServer = { viewModel.deleteServer(it) },
                                onRefreshPings = { viewModel.refreshServerPings() }
                            )
                        }

                        is Screen.Equalizer -> {
                            EquinoxEqualizerView(
                                activePreset = soundPreset,
                                onSelectPreset = { viewModel.setSoundPreset(it) }
                            )
                        }

                        is Screen.Settings -> {
                            SettingsScreen(
                                cacheSize = cacheSize,
                                onClearCache = { viewModel.clearCache() },
                                onNavigateWhatsNew = { viewModel.navigateTo(Screen.WhatsNew) },
                                onNavigateAbout = { viewModel.navigateTo(Screen.About) },
                                onNavigateServers = { viewModel.navigateTo(Screen.Servers) },
                                onNavigateEqualizer = { viewModel.navigateTo(Screen.Equalizer) }
                            )
                        }

                        is Screen.WhatsNew -> {
                            WhatsNewScreen(
                                expandedIndex = expandedWhatsNew,
                                onToggleAccordion = { viewModel.toggleWhatsNewExpansion(it) },
                                onBack = { viewModel.navigateTo(Screen.Settings) }
                            )
                        }

                        is Screen.About -> {
                            AboutScreen(
                                onBack = { viewModel.navigateTo(Screen.Settings) }
                            )
                        }

                        else -> {}
                    }

                    // Floating Voice Command Recognition Overlay
                    val showVoiceBar = voiceState !is VoiceUiState.Idle
                    val isVoiceListening = voiceState is VoiceUiState.Listening
                    val voiceTitle = when (voiceState) {
                        is VoiceUiState.Listening -> "Voice Listening..."
                        is VoiceUiState.Processing -> "Processing Command..."
                        is VoiceUiState.Success -> "Voice Command Executed"
                        is VoiceUiState.Error -> "Voice Command Error"
                        is VoiceUiState.Idle -> ""
                    }
                    val voiceDesc = when (val s = voiceState) {
                        is VoiceUiState.Listening -> "Say 'Play Next', 'Show Lyrics', 'Driver Mode'..."
                        is VoiceUiState.Processing -> "\"${s.rawSpeech}\""
                        is VoiceUiState.Success -> s.message
                        is VoiceUiState.Error -> s.message
                        is VoiceUiState.Idle -> ""
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = showVoiceBar,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = EvCardElevated,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, EvCyan),
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isVoiceListening) {
                                    CircularProgressIndicator(
                                        color = EvCyan,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = EvCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = voiceTitle,
                                        fontWeight = FontWeight.Bold,
                                        color = EvCyan,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = voiceDesc,
                                        color = EvTextPrimary,
                                        fontSize = 14.sp
                                    )
                                }

                                IconButton(onClick = { viewModel.voiceManager.stopListening() }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = EvTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Plex TV PIN Dialog
    if (showPlexDialog) {
        PlexPinDialog(
            pinState = plexPinState,
            isLoading = isPlexLoading,
            onDismiss = { showPlexDialog = false },
            onRetry = { viewModel.startPlexPinFlow() },
            onSimulateSuccess = { viewModel.simulatePlexPinApproval() },
            onDirectTokenConnect = { name, url, token ->
                viewModel.connectPlexDirect(name, url, token)
            }
        )
    }
}
