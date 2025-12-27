# Mantener clases de OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# DataStore
-keep class androidx.datastore.** { *; }

# Simple XML (si lo usas)
-keep class org.simpleframework.** { *; }
-dontwarn org.simpleframework.**