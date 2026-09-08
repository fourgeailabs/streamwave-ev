# StreamWave 🚗🎵
**Version 1.02.00**  
Created by **[FourgeAI LABS](https://github.com/fourgeailabs)**  
Repository: **[https://github.com/fourgeailabs/streamwave](https://github.com/fourgeailabs/streamwave)**

StreamWave is an automotive-grade, high-fidelity music streaming client and media player specifically engineered for the **Chevrolet Equinox EV** (and adaptable for other electric vehicles, tablets, and phones). Heavily inspired by the iconic **Plexamp** aesthetic, StreamWave features a mature, deep obsidian and warm amber design system, interactive loudness waveforms, and home hubs connecting directly to your personal home media servers—including **Plex** (with TV PIN 4-digit code sign-in), **Subsonic / Navidrome / Airsonic**, and **Jellyfin / Emby**.

---

## ✨ Key Features

### 🎧 StreamWave Audiophile Visual Architecture
- **Obsidian & Warm Amber Design System**: Pure black/obsidian canvas designed for high-contrast OLED center stacks, paired with vibrant amber highlights and refined typography.
- **Interactive Loudness Waveform Scrubber**: True amplitude bar visualization replacing generic seekbars. Scrub directly across dynamic soundwave peaks with microsecond precision.
- **Lossless & Direct Play Badges**: Explicit audiophile tags (`FLAC 24-bit / 96 kHz`, `Direct Play`, `Direct Stream`) on every track and album.
- **StreamWave Home Hubs**:
  - **Recent Plays**: Horizontal carousel with album art and playback badges.
  - **Stations & Mixes**: Instant access to *Library Radio*, *Sonic Flow*, and *Time Travel* mix generators.
  - **Heavy Rotation**: Quick-resume your top albums and tracks.

### 🚘 Distraction-Free Cockpit & EV Driver Mode
- **Native 17.7-inch Equinox EV Layout**: Adaptive layouts tailored for ultrawide EV touchscreens, landscape center stacks, and portrait phones.
- **High-Contrast Driver Mode HUD**: Giant 88dp+ tactile touch targets, minimized cognitive load, live audio spectrum pulse, and steering-wheel voice triggers.
- **Cabin Acoustic Soundstage EQ**: 5-band parametric equalizer tuned specifically for the Equinox EV cabin acoustics and Bose premium sound systems (Balanced, Driver Stage, Rear Passenger, Punchy EV Bass, and Vocal Clarity).
- **Zero-Cellular Offline Caching**: One-tap album downloads to local vehicle storage with a persistent "Downloaded Only" mode toggle for remote road trips.

### 📡 Home Media Server Ecosystem & Plex TV Authentication
- **Plex TV 4-Digit PIN Authentication**: Link your Plex account using genuine 4-character TV PIN pairing via `plex.tv/link`. Real-time status polling detects when you approve on another device.
- **Direct Plex Token Sign-In**: Dedicated token tab allowing manual connection using `X-Plex-Token` or custom internal server hostnames/IP addresses.
- **Subsonic, Navidrome & Airsonic**: Full token and salt authentication with fast folder browsing and cached metadata.
- **Jellyfin & Emby Integration**: Direct REST API connection to your Jellyfin libraries.
- **Live Latency & Server Status**: Monitor server health with live ping indicators and automatic fallback.

### 🎤 Synchronized Real-Time Karaoke Lyrics
- **Precision Karaoke Scrolling**: Auto-scrolling lyrics with glowing active line typography and dimmed future/past stanzas.
- **Interactive Tap-to-Seek**: Tap any lyric line to jump playback instantly to that millisecond timestamp.

### 🎙️ Hands-Free Voice Control
- **Steering Wheel Assistant**: Tap the microphone icon or vehicle voice button to speak commands without taking your eyes off the road:
  - *"Play [track / artist]"*
  - *"Pause"* / *"Resume"*
  - *"Next"* / *"Skip song"*
  - *"Previous"* / *"Rewind"*
  - *"Shuffle"* / *"Repeat"*
  - *"Turn on Driver Mode"* / *"Show Lyrics"*
  - *"Add to favorites"*

---

## 🛠️ Tech Stack & Architecture

- **Platform**: Modern Android (Kotlin, minSdk 24, targetSdk 36)
- **UI Framework**: Jetpack Compose with Material 3, custom StreamWave dark audiophile design tokens
- **Local Persistence**: Android Jetpack Room with KSP
- **Image & Artwork Loading**: Coil Compose
- **Network & Server APIs**: Retrofit2, OkHttp3, Moshi, Coroutines & Flow
- **Audio Engine**: Android MediaPlayer with foreground playback service and audio focus management
- **Voice Recognition**: Android SpeechRecognizer engine

---

## 📋 What's New / Release Notes

### [v1.02.00] - Current Release
- **StreamWave Global Branding**: Rebranded the application globally to StreamWave across all UI displays, headers, vehicle HUD, and service integrations.
- **Plex TV PIN Sign-In Engine Fix**: Fixed PIN generation to request real 4-digit codes from plex.tv, eliminating 25-character hash overflow and enabling seamless linking at plex.tv/link.
- **Direct Plex Token Sign-In**: Added a dedicated tab in the link dialog to connect directly using an X-Plex-Token or custom server IP/URL.
- **One-Tap plex.tv/link Launcher**: Integrated a direct link button to immediately open the Plex linking page in your device browser.
- **Persistent Client Identifier**: Stable hardware client identity saved to persistent storage for continuous Plex server authentication.
- **Null-Safe Server Discovery**: Fixed JSON auth token extraction and multi-server resource parsing for rock-solid connection reliability.

### [v1.01.00] - Previous Release
- **Audiophile Visual Architecture**: Complete visual overhaul replacing childish styles with signature dark obsidian & warm amber aesthetic.
- **Interactive Loudness Waveform Scrubber**: Dynamic amplitude bar waveform scrubber with tactile drag and seek.
- **StreamWave Home Hubs**: Added Recent Plays, Stations & Mixes (Library Radio, Sonic Flow, Time Travel), and Heavy Rotation.
- **StreamWave EV Car Mode Overhaul**: Distraction-free high-contrast cockpit with 88dp+ tactile touch targets, live audio spectrum pulse, and hands-free voice trigger.
- **Audiophile Badges**: Lossless FLAC 24-bit / 96 kHz and Direct Play indicators across player and library.
- **Karaoke Lyrics Polish**: Glowing typography, smooth centered scrolling, and instant tap-to-seek playback jump.
- **Plex TV PIN Link**: 4-digit TV authorization via plex.tv/link with auto-polling.

### [v1.00.00] - Initial Release
- **Plex TV PIN Flow**: Implemented standard 4-digit code linking via `plex.tv/api/v2/pins`.
- **Equinox EV & Automotive UI**: Built landscape ultrawide dashboard, Driver Mode, and tablet navigation rail.
- **Synchronized Lyrics Engine**: Added auto-scrolling synced lyrics with interactive tap-to-seek.
- **Room Database Offline Storage**: Added caching mechanism for offline road trip playback.
- **Hands-Free Voice Recognition**: Integrated driving voice commands for playback and navigation.
- **Soundstage Presets**: Equinox EV 6-speaker acoustic profiles.
- **FourgeAI LABS About & What's New**: Integrated accordion changelog and creator portfolio links.

---

## 🚀 Building & Releasing

Automated APK generation is powered by GitHub Actions in `.github/workflows/build.yml`. Every push or version tag `v*` compiles a signed debug APK and attaches it directly to GitHub Releases.

```bash
# To build locally:
gradle assembleDebug
```

---

## 📄 License & Credits
Developed by **[FourgeAI LABS](https://github.com/fourgeailabs)**.  
All rights reserved.
