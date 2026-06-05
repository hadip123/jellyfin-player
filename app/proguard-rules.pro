# Keep Retrofit and Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class dev.yourhandle.jellyfinplayer.data.model.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
