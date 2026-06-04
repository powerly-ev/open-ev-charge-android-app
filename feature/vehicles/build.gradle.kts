import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.compose)
    alias(libs.plugins.powerly.koin)
    alias(libs.plugins.powerly.serialization)
}

android {
    namespace = "${MyProject.NAMESPACE}.vehicles"
}

dependencies {
    implementation(projects.common.navigation)
    implementation(projects.common.ui)
    implementation(projects.core.model)
    implementation(projects.core.network)
}