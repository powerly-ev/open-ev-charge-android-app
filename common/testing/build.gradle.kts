import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
}

android {
    namespace = "${MyProject.NAMESPACE}.testing"
}


dependencies {
    //--------- test libraries
    api(libs.junit)
    // Robolectric environment
    api(libs.test.core)
    // Mockito framework
    api(libs.mockito.core)
    api(libs.junit.ktx)
    // Coroutines test
    api(libs.kotlinx.coroutines.test)
    // Instrumented tests
    api(libs.junit.ktx)
    api(libs.test.runner)
    api(libs.test.rules)
    // Optional -- UI testing with Espresso
    api(libs.espresso.core)
    // Optional -- UI testing with UI Automator
    api(libs.uiautomator)
}