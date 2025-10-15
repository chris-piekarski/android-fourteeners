# User Experience Documentation

## Navigation Flow

The following diagram shows the screen navigation flow and user journey through the Android Fourteeners app:

```mermaid
graph TD
    A[App Launch] --> B[Home Screen]
    B --> C{User Choice}

    C -->|Bag New Peak| D[Hike Activity]
    C -->|Add Old Peak| E[Add Bag Activity]
    C -->|View Register| F[Register Activity]
    C -->|Menu| G[Options Menu]

    G --> H[Settings]
    G --> I[Help]
    G --> J[License]
    G --> K[Leave No Trace]

    D --> L[Select Mountain]
    L --> M[Track Hike]
    M --> N[Complete Summit]
    N --> B

    E --> O[Fill Form]
    O --> P[Select Peak]
    O --> Q[Set Date/Time]
    O --> R[Add Photo]
    O --> S[Add Notes]
    P & Q & R & S --> T[Save Entry]
    T --> B

    F --> U[View History]
    U --> V[Browse Entries]
    V --> B

    H --> W[General Settings]
    H --> X[ICE Settings]
    W & X --> B

    style A fill:#e1f5fe
    style B fill:#fff3e0
    style N fill:#c8e6c9
    style T fill:#c8e6c9
```

## Screen Features

### 1. Home Screen (HomeActivity)
Main dashboard displaying user progress and navigation options.

```mermaid
graph LR
    subgraph "Home Screen Features"
        A[Progress Bar<br/>X/53 peaks]
        B[Statistics Display]
        C[Navigation Buttons]

        B --> B1[Total Unique Summits]
        B --> B2[Total Summit Count]
        B --> B3[Last Summit Name]

        C --> C1[Bag New Peak]
        C --> C2[Add Old Peak]
        C --> C3[View Register]
    end
```

**Key Features:**
- Visual progress tracking (0-53 fourteeners)
- Quick stats at a glance
- Three main action buttons
- Options menu access

### 2. Hike Activity
For tracking a current hike with GPS assistance.

```mermaid
graph TD
    subgraph "Hike Activity Flow"
        A[GPS Location] --> B[Calculate Nearest Peaks]
        B --> C[Display List]
        C --> D{User Selection}
        D -->|Auto Select| E[Nearest Peak]
        D -->|Manual Pick| F[Peak List]
        E & F --> G[Start Tracking]
        G --> H[Record Data]
        H --> I[Summit Entry]
    end
```

**Key Features:**
- GPS-based nearest mountain detection
- Shows 10 nearest fourteeners
- Manual peak selection option
- Real-time location tracking

### 3. Add Bag Activity
For manually adding past summit attempts.

```mermaid
stateDiagram-v2
    [*] --> FormDisplay
    FormDisplay --> RangeSelection
    RangeSelection --> PeakSelection
    PeakSelection --> DatePicking
    DatePicking --> TimePicking
    TimePicking --> PhotoAdding
    PhotoAdding --> NotesEntry
    NotesEntry --> Validation
    Validation --> SaveEntry: Valid
    Validation --> FormDisplay: Invalid
    SaveEntry --> [*]
```

**Key Features:**
- Mountain range filtering
- Peak selection from dropdown
- Date and time pickers
- Photo proof upload
- Notes field for details
- Form validation

### 4. Register Activity
Summit history viewer with chronological entries.

```mermaid
graph TD
    subgraph "Register Display"
        A[Database Query] --> B[Cursor Adapter]
        B --> C[List View]
        C --> D[Entry Items]

        D --> E[Peak Name]
        D --> F[Elevation]
        D --> G[Summit Status]
        D --> H[Date/Time]

        G --> G1[✓ Summited]
        G --> G2[⚠ Attempted]
    end
```

**Key Features:**
- Chronologically sorted entries
- Summit vs attempt distinction
- Formatted date display
- Mountain details per entry

### 5. Settings Activity
User preferences and emergency contact configuration.

```mermaid
graph LR
    subgraph "Settings Structure"
        A[Settings Root] --> B[General]
        A --> C[ICE Info]

        B --> B1[User Name]
        B --> B2[App Preferences]

        C --> C1[Contact Name]
        C --> C2[Contact Email]
        C --> C3[Contact Phone]
    end
```

**Key Features:**
- Two-pane layout on tablets
- General preferences
- In Case of Emergency (ICE) contact info
- Preference persistence

## User Journey Scenarios

### Scenario 1: First Time User
```mermaid
journey
    title First Time User Journey
    section Setup
      Open App: 5: User
      View Empty Stats: 3: User
      Navigate to Settings: 5: User
      Add ICE Info: 5: User
    section First Summit
      Click Bag New Peak: 5: User
      GPS Detects Location: 5: System
      Select Mountain: 5: User
      Complete Hike: 3: User
      Save Entry: 5: User
    section Review
      View Updated Stats: 5: User
      Check Register: 5: User
      See First Entry: 5: User
```

### Scenario 2: Experienced User Adding Past Summits
```mermaid
journey
    title Adding Historical Summits
    section Navigate
      Open App: 5: User
      Click Add Old Peak: 5: User
    section Data Entry
      Select Range: 5: User
      Choose Peak: 5: User
      Set Date: 4: User
      Set Times: 4: User
      Add Photo: 3: User
      Write Notes: 4: User
    section Complete
      Save Entry: 5: User
      View Updated Progress: 5: User
```

## Accessibility Features

```mermaid
mindmap
  root((Accessibility))
    Visual
      High contrast text
      Large touch targets
      Clear icons
    Navigation
      Hardware back button
      Consistent menu placement
      Predictable navigation
    Data Entry
      Date/time pickers
      Dropdown menus
      Photo selection
    Feedback
      Toast messages
      Progress indicators
      Success confirmations
```

## Error Handling

```mermaid
graph TD
    A[User Action] --> B{Validation}
    B -->|Valid| C[Process Action]
    B -->|Invalid| D[Show Error]

    C --> E{Success?}
    E -->|Yes| F[Show Success Toast]
    E -->|No| G[Show Error Toast]

    D --> H[Highlight Field]
    G --> I[Log Error]

    F --> J[Update UI]
    H --> A
    I --> A
```

## Performance Considerations

- **GPS Updates**: Passive provider to conserve battery
- **Database Operations**: Read operations on UI thread (to be improved)
- **Image Handling**: Bitmap sampling to prevent OOM errors
- **List Views**: ViewHolder pattern for smooth scrolling
- **Location Services**: HandlerThread for background location updates

## Future UX Improvements

1. **Modern Navigation**: Bottom navigation or navigation drawer
2. **Material Design**: Update to Material 3 components
3. **Dark Mode**: System-wide dark theme support
4. **Offline Maps**: Trail maps for offline use
5. **Social Features**: Share summits, compare with friends
6. **Achievements**: Gamification elements
7. **Weather Integration**: Current conditions at peaks
8. **Trail Conditions**: Community-sourced updates