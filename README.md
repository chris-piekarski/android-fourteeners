# android-fourteeners

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

Contribute what you can when you can and only if you *want* to!
