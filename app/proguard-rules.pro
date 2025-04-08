-keep class com.powerly.core.model.** { *; }
-dontwarn com.powerly.payment.BR

# Retrofit, Gson, and Coroutines
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keep class kotlin.coroutines.** { *; }
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keep public class * extends android.app.**

-dontwarn com.google.j2objc.annotations.ReflectionSupport
-dontwarn com.google.j2objc.annotations.RetainedWith
-dontwarn kotlinx.parcelize.Parceler
-dontwarn kotlinx.parcelize.Parcelize

-keep class com.powerly.lib.managers.MessagingService { *; }

# Remove Logs (more concise)
-assumenosideeffects class android.util.Log {
    public static *** *(...);
}