import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.compose)
    alias(libs.plugins.powerly.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.orders"
}

dependencies {
    implementation(projects.common.navigation)
    implementation(projects.core.managers)
    implementation(projects.common.ui)
}