# Jellyfin Player

A lightweight Android music player for Jellyfin, with a clean/minimal/blurry UI inspired by Tidal.

![screenshot](docs/screenshot.png)

## Requirements

- Jellyfin server >= 10.9
- Android 8.0+ (API 26+)

## Setup

1. Clone the repository
2. Open in Android Studio, or download the APK from GitHub Actions artifacts

## Configuration

Server URL and credentials are entered at first launch. No hardcoded configuration.

## Architecture

```
LoginScreen -> LibraryScreen -> PlayerScreen
                                  |
                           PlayerViewModel
                                  |
                     PlayerController (ExoPlayer/Media3)
                                  |
                           Jellyfin API
```

## Build

```bash
./gradlew assembleDebug
```

APK will be at `app/build/outputs/apk/debug/app-debug.apk`.
