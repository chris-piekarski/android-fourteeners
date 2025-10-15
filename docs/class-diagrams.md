# UML Class Diagrams

## Core Classes Overview

```mermaid
classDiagram
    class SummitRegister {
        <<Application>>
        -mTracker: Any?
        +getDefaultTracker(): Any?
    }

    class Activity {
        <<abstract>>
        +onCreate(Bundle)
        +onResume()
        +onPause()
        +onDestroy()
    }

    class HomeActivity {
        -stats1: TextView
        -stats2: TextView
        -stats3: TextView
        -progress: ProgressBar
        +showRegister(View)
        +showBagNewPeak(View)
        +showAddOldPeak(View)
        -refreshStats()
        -showToastBox(Int)
    }

    class HikeActivity {
        -dl: DeviceLocation
        -mAdapter: MyAdapter
        +onCreate(Bundle)
        +onResume()
        +manualPick(View)
    }

    class AddBagActivity {
        -mRanges: Spinner
        -mPeaks: Spinner
        -mStartTime: TextView
        -mEndTime: TextView
        -mDate: TextView
        -mNotes: EditText
        -mProof: ImageView
        -mStartDate: RegisterDate
        -mEndDate: RegisterDate
        -mProofPath: String
        +addAddPeak(View)
        +cancelAddPeak(View)
        +sendGetProofIntent(View)
        +showDatePickerDialog(View)
        +showStartTimePickerDialog(View)
        +showEndTimePickerDialog(View)
    }

    class RegisterActivity {
        -mAdapter: SimpleCursorAdapter
        +onCreate(Bundle)
        +onResume()
    }

    Activity <|-- HomeActivity
    Activity <|-- HikeActivity
    Activity <|-- AddBagActivity
    Activity <|-- RegisterActivity
    SummitRegister <-- HomeActivity : uses
```

## Data Layer Classes

```mermaid
classDiagram
    class Register {
        <<Singleton>>
        -INSTANCE: Register?
        -tag: String
        -registerHelper: RegisterHelper
        -database: SQLiteDatabase
        +getInstance(Context): Register
        +getTotalEntries(): Int
        +getTotalSummits(): Int
        +getTotalSummits(String): Int
        +getTotalUniqueSummits(): Int
        +getLastEntry(): RegisterEntry?
    }

    class RegisterEntry {
        -context: Context
        -mountain: Mountain?
        -startTime: String?
        -endTime: String?
        -distance: String?
        -startElevation: Int
        -endElevation: Int
        -peekElevation: Int
        -summit: Int
        -startLatLoc: Double
        -startLongLoc: Double
        -endLatLoc: Double
        -endLongLoc: Double
        -notes: String?
        -rowId: Long
        -proofPath: String?
        +createEntry(): Boolean
        +readEntry(Int): Boolean
        +updateEntry(): Boolean
        +deleteEntry(): Boolean
        +setMountain(Mountain)
        +setReachedSummit(Boolean)
        +setStartLoc(Double, Double, Int)
        +setEndLoc(Double, Double, Int)
        +getMountainName(): String?
        +getMountainRange(): String?
    }

    class RegisterHelper {
        <<SQLiteOpenHelper>>
        +DATABASE_VERSION: Int
        +DATABASE_NAME: String
        +TABLE_NAME: String
        +MNT_NAME: String
        +MNT_RANGE: String
        +START_TIME: String
        +END_TIME: String
        +START_ELEVATION: String
        +NOTES: String
        +SUMMIT: String
        +onCreate(SQLiteDatabase)
        +onUpgrade(SQLiteDatabase, Int, Int)
    }

    Register --> RegisterHelper : uses
    Register --> RegisterEntry : creates
    RegisterEntry --> RegisterHelper : uses
    RegisterEntry --> Mountain : references
```

## Mountain Data Classes

```mermaid
classDiagram
    class Mountains {
        <<Singleton>>
        -INSTANCE: Mountains?
        -context: Context
        -mountains: TreeMap~String-Mountain~
        -ranges: TreeMap~String-ArrayList~
        -tracker: Any?
        +getInstance(Context): Mountains
        +getRanges(): Array~String~
        +getNamesInRange(String): Array~String~?
        +getAllPeakNames(): Array~String~
        +getSize(): Int
        +getMountain(String): Mountain?
        +getMountain(String, String): Mountain?
        -parseFourteeners()
    }

    class Mountain {
        -name: String
        -range: String
        -county: String
        -rank: Int
        -elevation: Int
        -longitude: Double
        -latitude: Double
        +getLongitude(): Double
        +getLatitude(): Double
        +getName(): String
        +getRange(): String
        +getCounty(): String
        +getElevation(): Int
        +toString(): String
    }

    Mountains "1" --> "*" Mountain : manages
```

## Location Services Classes

```mermaid
classDiagram
    class DeviceLocation {
        -context: Context
        -locationManager: LocationManager
        -locationListener: LocationListener
        -lastMockLocation: Location?
        -lastGPSLocation: Location?
        -lastNetworkLocation: Location?
        -lastLocation: Location?
        -locationType: LocationType?
        -looper: Looper?
        +getNetworkUpdates()
        +getGPSUpdates()
        +getPassiveUpdates()
        +getMockUpdates()
        +getLastGPSLocation(): Location?
        +getLastUpdateLocation(): Location?
        +getLastLocationType(): LocationType?
        +getNearestMountains(Int): ArrayList~Mountain~
    }

    class LocationType {
        <<enumeration>>
        GPS
        NETWORK
        MOCK
    }

    DeviceLocation --> LocationType : uses
    DeviceLocation --> Mountains : queries
    DeviceLocation --> Mountain : returns list
```

## Utility Classes

```mermaid
classDiagram
    class RegisterDate {
        -calendar: Calendar
        -format: SimpleDateFormat
        -formatter: String
        +RegisterDate()
        +RegisterDate(String)
        +parseStrDate(String): Boolean
        +getStrDate(): String
        +setDate(Int, Int, Int)
        +setTime(Int, Int, Int)
        +getIntField(Int): Int
        +getFormatter(): String
    }

    class SRLOG {
        <<utility>>
        -silent: Boolean
        +v(String, String)$
        +d(String, String)$
        +i(String, String)$
        +w(String, String)$
        +e(String, String)$
        +wtf(String, String)$
    }
```

## Activity Relationships

```mermaid
classDiagram
    HomeActivity --> Register : queries stats
    HomeActivity --> RegisterActivity : navigates to
    HomeActivity --> HikeActivity : navigates to
    HomeActivity --> AddBagActivity : navigates to

    HikeActivity --> DeviceLocation : uses GPS
    HikeActivity --> Mountains : gets nearest

    AddBagActivity --> RegisterEntry : creates
    AddBagActivity --> Mountains : selects from
    AddBagActivity --> RegisterDate : uses for dates

    RegisterActivity --> RegisterHelper : queries database
    RegisterActivity --> RegisterEntry : displays
```

## Database Schema Classes

```mermaid
classDiagram
    class DatabaseSchema {
        <<table: register>>
        _id: INTEGER PRIMARY KEY
        mnt_name: TEXT
        mnt_range: TEXT
        start_time: TEXT
        end_time: TEXT
        start_elevation: INTEGER
        end_elevation: INTEGER
        peek_elevation: INTEGER
        start_location: TEXT
        end_location: TEXT
        distance: TEXT
        summit: INTEGER
        notes: TEXT
        proof: TEXT
    }

    RegisterHelper --> DatabaseSchema : creates/manages
    RegisterEntry --> DatabaseSchema : CRUD operations
```

## Adapter Pattern

```mermaid
classDiagram
    class SimpleCursorAdapter {
        <<Android Framework>>
        +SimpleCursorAdapter(Context, int, Cursor, String[], int[], int)
        +setViewText(TextView, String)
    }

    class MyAdapter {
        <<inner class>>
        -context: Context
        +setViewText(TextView, String)
        -convertDate(String): Date
    }

    class ArrayAdapter~T~ {
        <<Android Framework>>
        +ArrayAdapter(Context, int, List~T~)
        +getView(int, View, ViewGroup): View
    }

    class HikeAdapter {
        <<inner class>>
        +getView(int, View, ViewGroup): View
    }

    SimpleCursorAdapter <|-- MyAdapter
    ArrayAdapter <|-- HikeAdapter
    RegisterActivity --> MyAdapter : uses
    HikeActivity --> HikeAdapter : uses
```

## Singleton Pattern Implementation

```mermaid
classDiagram
    class SingletonPattern {
        <<interface>>
        +getInstance(Context): T$
    }

    class Register {
        -INSTANCE: Register?$
        -Register(Context)
        +getInstance(Context): Register$
    }

    class Mountains {
        -INSTANCE: Mountains?$
        -Mountains(Context)
        +getInstance(Context): Mountains$
    }

    SingletonPattern <|.. Register : implements
    SingletonPattern <|.. Mountains : implements
```

## Settings Architecture

```mermaid
classDiagram
    class SettingsActivity {
        <<PreferenceActivity>>
        -ALWAYS_SIMPLE_PREFS: Boolean$
        +onPostCreate(Bundle)
        +onBuildHeaders(List~Header~)
        +isValidFragment(String): Boolean
        -setupSimplePreferencesScreen()
    }

    class GeneralPreferenceFragment {
        <<PreferenceFragment>>
        +onCreate(Bundle)
    }

    class ICEPreferenceFragment {
        <<PreferenceFragment>>
        +onCreate(Bundle)
    }

    SettingsActivity --> GeneralPreferenceFragment : contains
    SettingsActivity --> ICEPreferenceFragment : contains
```

## Exception and Error Handling

```mermaid
classDiagram
    class ErrorHandling {
        <<pattern>>
        +try-catch blocks
        +null checks
        +validation
        +logging
    }

    RegisterEntry --> ErrorHandling : uses
    Mountains --> ErrorHandling : uses
    DeviceLocation --> ErrorHandling : uses

    class SRLOG {
        +e(tag: String, msg: String)
        +w(tag: String, msg: String)
    }

    ErrorHandling --> SRLOG : logs errors
```

## Design Patterns Used

1. **Singleton Pattern**: Register, Mountains classes
2. **Adapter Pattern**: Custom adapters for ListView
3. **Observer Pattern**: LocationListener for GPS updates
4. **Template Method**: Activity lifecycle methods
5. **Factory Pattern**: RegisterEntry creation
6. **Repository Pattern**: Register class for data access
7. **Builder Pattern**: Implicit in Android Views and Dialogs

## Class Responsibilities

| Class | Primary Responsibility | Design Pattern |
|-------|----------------------|----------------|
| SummitRegister | Application-wide state | Application class |
| Register | Data access layer | Singleton, Repository |
| RegisterEntry | Data model | Entity, CRUD |
| RegisterHelper | Database management | SQLiteOpenHelper |
| Mountains | Mountain data provider | Singleton |
| Mountain | Data model | Value object |
| DeviceLocation | Location services | Service, Observer |
| RegisterDate | Date formatting | Utility |
| SRLOG | Logging | Utility, Facade |
| Activities | UI Controllers | MVC Controller |