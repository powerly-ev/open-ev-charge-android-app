import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.compose)
}

android {
    namespace = "${MyProject.NAMESPACE}.ui"
}

dependencies {
    implementation(projects.common.lib)
    "preReleaseApi"(libs.androidx.ui.tooling)

    api(libs.compose.foundation)
    api(libs.compose.material3)
    // UI Tests
    androidTestApi(libs.androidx.ui.test.junit4)
    debugApi(libs.androidx.ui.test.manifest)

    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    api(libs.androidx.material.icons.core)
    // Optional - Add full set of material icons
    api(libs.androidx.material.icons.extended)
    // Optional - Add window size utils
    api(libs.androidx.material3.window.size)

    // Optional - Integration with activities
    api(libs.androidx.activity.compose)
    // Optional - Integration with ViewModels
    api(libs.androidx.lifecycle.viewmodel.compose)

    // Optional - Integration with LiveData
    api(libs.androidx.runtime.livedata)

    api(libs.androidx.lifecycle.runtime.compose)

    //compose network image
    api(libs.compose.coil)
    api(libs.compose.coil.gif)

    api(libs.navigation.compose)

    api(libs.androidx.paging.compose)

    implementation(libs.lottie.compose)

    // google map
    implementation(libs.maps.compose)
    implementation(libs.maps.compose.utils)
}