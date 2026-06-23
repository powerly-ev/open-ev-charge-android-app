import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.feature)
    alias(libs.plugins.powerly.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.account"
}

dependencies {
    implementation(projects.core.managers)
    //QR Generator
    implementation(libs.custom.qr.generator)
}