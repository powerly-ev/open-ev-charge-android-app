import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.compose)
    alias(libs.plugins.powerly.koin)
    alias(libs.plugins.powerly.serialization)
}

android {
    namespace = "${MyProject.NAMESPACE}.user"
}

dependencies {
    implementation(projects.common.navigation)
    implementation(projects.core.managers)
    implementation(projects.common.ui)
}