import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.compose)
    alias(libs.plugins.powerly.hilt)
}

android {
    namespace = "${MyProject.NAMESPACE}.home"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
    implementation(projects.feature.home.map)
    implementation(projects.feature.home.scan)
    implementation(projects.feature.home.orders)
    implementation(projects.feature.home.account)
}