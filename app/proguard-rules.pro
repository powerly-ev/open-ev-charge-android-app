-keep class com.powerly.core.model.** { *; }
-keep class com.powerly.lib.managers.MessagingService { *; }
-dontwarn com.powerly.payment.BR

-keep public class * extends android.app.**
-keep public class com.google.j2objc.** { public *; }

# Google Places
-keep class com.google.android.libraries.places.** { *; }

-dontwarn com.google.j2objc.**
-dontwarn kotlinx.parcelize.**

# Koin Core & General
-keep class org.koin.** { *; }
-keep class org.koin.core.registry.** { *; }
-keep class org.koin.core.scope.** { *; }
-keep class org.koin.androidx.viewmodel.scope.** { *; }
-keep class org.koin.androidx.scope.** {*;}

# If using Koin KSP generated code (highly recommended for modern Koin)
-keep class org.koin.ksp.generated.** { *; }

# Remove Logs (more concise)
-assumenosideeffects class android.util.Log {
    public static *** *(...);
}

