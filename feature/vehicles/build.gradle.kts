import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.feature)
    alias(libs.plugins.powerly.koin)
    alias(libs.plugins.powerly.serialization)
    alias(libs.plugins.powerly.test)
}

android {
    namespace = "${MyProject.NAMESPACE}.vehicles"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.kotlinx.datetime)
}