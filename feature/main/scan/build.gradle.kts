import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.feature)
    alias(libs.plugins.powerly.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.scan"
}

dependencies {

    //For QR code scanner
    implementation(libs.zxing.android) { isTransitive = false }
    implementation(libs.zxing.core)
}