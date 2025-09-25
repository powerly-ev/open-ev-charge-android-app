import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.compose)
    alias(libs.plugins.powerly.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.account"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
    //QR Generator
    implementation(libs.custom.qr.generator)
}