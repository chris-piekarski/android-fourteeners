# Keep models and Room schema
-keep class androidx.room.** { *; }
-keep class **Database { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn javax.annotation.**
-dontwarn org.jetbrains.annotations.**
