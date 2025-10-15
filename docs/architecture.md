# System Architecture Documentation

## High-Level Architecture

The Android Fourteeners app follows a layered architecture pattern with clear separation of concerns:

```mermaid
graph TB
    subgraph "Presentation Layer"
        A[Activities]
        B[XML Layouts]
        C[Resources]
    end

    subgraph "Business Logic Layer"
        D[Register Manager]
        E[Mountains Manager]
        F[Location Services]
    end

    subgraph "Data Layer"
        G[SQLite Database]
        H[XML Data Files]
        I[SharedPreferences]
    end

    subgraph "External Services"
        J[GPS Provider]
        K[File System]
        L[Camera/Gallery]
    end

    A --> D
    A --> E
    A --> F
    D --> G
    E --> H
    F --> J
    A --> I
    A --> K
    A --> L

    style A fill:#e3f2fd
    style D fill:#fff3e0
    style G fill:#e8f5e9
    style J fill:#fce4ec
```

## Component Architecture

```mermaid
graph LR
    subgraph "Application Component"
        APP[SummitRegister<br/>Application]
    end

    subgraph "Activity Components"
        HOME[HomeActivity]
        HIKE[HikeActivity]
        ADD[AddBagActivity]
        REG[RegisterActivity]
        SET[SettingsActivity]
        HELP[HelpActivity]
        LIC[LicenseActivity]
        LNT[LeaveNoTraceActivity]
    end

    subgraph "Data Components"
        REGMGR[Register<br/>Singleton]
        MNTMGR[Mountains<br/>Singleton]
        LOC[DeviceLocation]
    end

    subgraph "Storage Components"
        DB[RegisterHelper<br/>SQLiteOpenHelper]
        XML[mountain_data.xml]
        PREF[SharedPreferences]
    end

    APP --> HOME
    HOME --> HIKE
    HOME --> ADD
    HOME --> REG
    HOME --> SET

    HOME --> REGMGR
    ADD --> REGMGR
    REG --> REGMGR
    HIKE --> MNTMGR
    ADD --> MNTMGR
    HIKE --> LOC

    REGMGR --> DB
    MNTMGR --> XML
    SET --> PREF
```

## Data Flow Architecture

```mermaid
sequenceDiagram
    participant User
    participant Activity
    participant Manager
    participant Database
    participant View

    User->>Activity: Interaction
    Activity->>Manager: Request Data
    Manager->>Database: Query
    Database-->>Manager: Result Set
    Manager-->>Activity: Processed Data
    Activity->>View: Update UI
    View-->>User: Display
```

## Singleton Pattern Implementation

```mermaid
classDiagram
    class Register {
        -INSTANCE: Register?
        -context: Context
        -registerHelper: RegisterHelper
        -database: SQLiteDatabase
        +getInstance(Context): Register
        +getTotalEntries(): Int
        +getTotalSummits(): Int
        +getTotalUniqueSummits(): Int
        +getLastEntry(): RegisterEntry?
    }

    class Mountains {
        -INSTANCE: Mountains?
        -context: Context
        -mountains: TreeMap~String, Mountain~
        -ranges: TreeMap~String, ArrayList~String~~
        +getInstance(Context): Mountains
        +getMountain(String): Mountain?
        +getAllPeakNames(): Array~String~
        +getNamesInRange(String): Array~String~?
    }

    Register ..> RegisterHelper : uses
    Mountains ..> Mountain : manages
```

## Location Service Architecture

```mermaid
graph TD
    subgraph "Location Component"
        A[DeviceLocation]
        B[LocationManager]
        C[LocationListener]
        D[HandlerThread]
        E[Looper]
    end

    subgraph "Location Providers"
        F[GPS Provider]
        G[Network Provider]
        H[Passive Provider]
    end

    subgraph "Location Data"
        I[Last GPS Location]
        J[Last Network Location]
        K[Last Passive Location]
    end

    A --> B
    A --> C
    A --> D
    D --> E
    B --> F
    B --> G
    B --> H
    C --> I
    C --> J
    C --> K

    style A fill:#e1f5fe
    style B fill:#fff9c4
    style F fill:#c8e6c9
```

## Activity Lifecycle Management

```mermaid
stateDiagram-v2
    [*] --> onCreate
    onCreate --> onStart
    onStart --> onResume
    onResume --> Running
    Running --> onPause: Another activity
    onPause --> onStop: Not visible
    onStop --> onDestroy: Finishing
    onDestroy --> [*]

    onStop --> onRestart: Coming back
    onRestart --> onStart
    onPause --> onResume: Regain focus

    note right of onCreate
        - Initialize views
        - Setup listeners
        - Load data
    end note

    note right of onResume
        - Refresh stats
        - Start location updates
    end note

    note right of onPause
        - Save data
        - Stop updates
    end note
```

## Dependency Graph

```mermaid
graph TD
    subgraph "Android Framework"
        SDK[Android SDK]
        ANDROIDX[AndroidX Libraries]
    end

    subgraph "Kotlin Runtime"
        KTX[Kotlin Extensions]
        STDLIB[Kotlin StdLib]
    end

    subgraph "Third Party"
        PLAY[Play Services<br/>Currently Disabled]
    end

    subgraph "Application"
        APP[App Module]
    end

    APP --> SDK
    APP --> ANDROIDX
    APP --> KTX
    APP --> STDLIB
    APP -.-> PLAY

    style PLAY stroke-dasharray: 5 5
```

## Build Architecture

```mermaid
graph LR
    subgraph "Source Sets"
        MAIN[main/kotlin]
        TEST[test/kotlin]
        ANDROID_TEST[androidTest/kotlin]
    end

    subgraph "Resources"
        LAYOUTS[layouts]
        VALUES[values]
        DRAWABLES[drawables]
        XML[xml configs]
    end

    subgraph "Build Process"
        COMPILE[Kotlin Compiler]
        AAPT[AAPT2]
        DEX[D8/R8]
        PACK[APK Packager]
    end

    subgraph "Output"
        DEBUG[Debug APK]
        RELEASE[Release APK]
    end

    MAIN --> COMPILE
    LAYOUTS --> AAPT
    VALUES --> AAPT
    DRAWABLES --> AAPT
    XML --> AAPT

    COMPILE --> DEX
    AAPT --> PACK
    DEX --> PACK

    PACK --> DEBUG
    PACK --> RELEASE
```

## Module Structure

```mermaid
graph TD
    subgraph "app"
        subgraph "src/main/kotlin/com/cpiekarski/fourteeners"
            A[SummitRegister.kt]

            subgraph "activities"
                B[HomeActivity.kt]
                C[HikeActivity.kt]
                D[AddBagActivity.kt]
                E[RegisterActivity.kt]
                F[SettingsActivity.kt]
            end

            subgraph "register"
                G[Register.kt]
                H[RegisterEntry.kt]
                I[RegisterHelper.kt]
            end

            subgraph "utils"
                J[DeviceLocation.kt]
                K[Mountain.kt]
                L[Mountains.kt]
                M[RegisterDate.kt]
                N[SRLOG.kt]
            end
        end
    end
```

## Security Architecture

```mermaid
graph TD
    subgraph "Security Layers"
        A[Permission System]
        B[Data Protection]
        C[Input Validation]
    end

    subgraph "Permissions"
        D[Location - Runtime]
        E[Storage - Runtime]
        F[Camera - Optional]
    end

    subgraph "Data Security"
        G[Local Storage Only]
        H[Parameterized Queries]
        I[No Cloud Sync]
    end

    subgraph "Validation"
        J[Form Validation]
        K[SQL Injection Prevention]
        L[File Path Validation]
    end

    A --> D
    A --> E
    A --> F

    B --> G
    B --> H
    B --> I

    C --> J
    C --> K
    C --> L

    style A fill:#ffebee
    style B fill:#e8f5e9
    style C fill:#fff3e0
```

## Performance Considerations

```mermaid
graph LR
    subgraph "Current Issues"
        A[DB on UI Thread]
        B[No View Binding]
        C[No Coroutines]
        D[Memory Leaks Risk]
    end

    subgraph "Proposed Solutions"
        E[Coroutines/RxJava]
        F[View Binding]
        G[Kotlin Flows]
        H[ViewModel Pattern]
    end

    A --> E
    B --> F
    C --> G
    D --> H

    style A fill:#ffcdd2
    style E fill:#c8e6c9
```

## Future Architecture Improvements

```mermaid
mindmap
  root((Modernization))
    MVVM
      ViewModel
      LiveData
      Data Binding
      Navigation Component
    Clean Architecture
      Domain Layer
      Use Cases
      Repository Pattern
      Dependency Injection
    Modern Libraries
      Hilt/Dagger
      Room Database
      Retrofit
      Compose UI
    Testing
      Unit Tests
      Integration Tests
      UI Tests
      Mock Framework
```

## Current Technical Debt

1. **Database Operations on UI Thread**: Risk of ANR
2. **Context in Singletons**: Potential memory leaks
3. **No Dependency Injection**: Tight coupling
4. **Manual View Management**: Boilerplate code
5. **No Architecture Pattern**: Mixed responsibilities
6. **Limited Test Coverage**: Only one test file
7. **Hardcoded Strings**: Some UI strings in code
8. **No Error Recovery**: Limited error handling

## Recommended Architecture Evolution

### Phase 1: MVVM Migration
- Implement ViewModels for each Activity
- Use LiveData for reactive UI
- Extract business logic from Activities

### Phase 2: Repository Pattern
- Create repository layer
- Abstract data sources
- Implement caching strategy

### Phase 3: Dependency Injection
- Add Hilt for DI
- Remove singletons
- Improve testability

### Phase 4: Modern UI
- Migrate to Compose
- Implement Material Design 3
- Add animations and transitions