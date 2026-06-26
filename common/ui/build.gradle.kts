import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.compose)
}

android {
    namespace = "${MyProject.NAMESPACE}.ui"
}

dependencies {
    // Foundational deps (mirrors what powerly.feature bundles for feature modules,
    // minus common/ui itself which would be self-circular).
    implementation(projects.common.navigation)
    implementation(projects.common.resources)
    implementation(projects.core.domain)
    implementation(projects.core.network)
    implementation(projects.core.managers)

    "preReleaseApi"(libs.androidx.ui.tooling)

    api(libs.compose.foundation)
    api(libs.compose.material3)
    // UI Tests
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Material icons — used internally for star ratings; not re-exported
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // Compose Activity integration — needed by feature screens
    api(libs.androidx.activity.compose)

    api(libs.androidx.lifecycle.runtime.compose)

    //compose network image
    api(libs.compose.coil)
    api(libs.compose.coil.gif)

    api(libs.navigation.compose)

    api(libs.androidx.paging.compose)

    implementation(libs.lottie.compose)

    // Google Maps
    implementation(libs.maps.compose)
    implementation(libs.maps.compose.utils)
}