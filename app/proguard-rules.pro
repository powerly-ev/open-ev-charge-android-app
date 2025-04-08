-keep class com.powerly.core.model.** { *; }
-keep class com.powerly.lib.managers.MessagingService { *; }
-dontwarn com.powerly.payment.BR

# Retrofit, Gson, and Coroutines
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keep class kotlin.coroutines.** { *; }
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keep public class * extends android.app.**
-keep public class com.google.j2objc.** { public *; }

-dontwarn com.google.j2objc.**
-dontwarn kotlinx.parcelize.**

# Remove Logs (more concise)
-assumenosideeffects class android.util.Log {
    public static *** *(...);
}
