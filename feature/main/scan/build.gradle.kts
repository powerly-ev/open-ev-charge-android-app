import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.compose)
    alias(libs.plugins.powerly.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.scan"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)

    //For QR code scanner
    implementation(libs.zxing.android) { isTransitive = false }
    implementation(libs.zxing.core)
}