# Android Fourteeners 🏔️

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)
[![Code of Conduct](https://img.shields.io/badge/Code%20of%20Conduct-Mountaineer%20Style-orange)](CODE_OF_CONDUCT.md)

A modern Android application for tracking summit attempts on Colorado's fourteeners (peaks above 14,000 feet), written in Kotlin and targeting Android API 33.

## Features

- Summit register for tracking your fourteener climbs
- GPS-based nearby peak detection
- Photo proof upload capability
- Progress tracking (X/53 fourteeners completed)
- Leave No Trace principles information

## Technical Details

- **Language**: Kotlin (migrated from Java)
- **Target SDK**: Android API 33 (Android 13)
- **Minimum SDK**: Android API 21 (Android 5.0)
- **Build System**: Gradle 7.6 with Android Gradle Plugin 7.4.2
- **Architecture**: SQLite database for local storage, AndroidX libraries

## Building

1. Clone the repository
2. Open in Android Studio
3. Build and run

```bash
./gradlew build
```

## Migration Notes

This project has been modernized from the original Java codebase:
- Converted from Java to Kotlin
- Updated from Android API 19 to API 33
- Migrated from legacy support libraries to AndroidX
- Updated build tools and dependencies

## Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

Also, please review our:
- [Code of Conduct](CODE_OF_CONDUCT.md) - Our mountaineering-themed community guidelines
- [Security Policy](SECURITY.md) - For reporting security vulnerabilities

Contribute what you can when you can and only if you *want* to! 🎿

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Colorado's majestic fourteeners for the inspiration
- The hiking community for beta and trail conditions
- All contributors who help improve this app
- Coffee shops at altitude for providing WiFi and warmth
