import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.compose)
    alias(libs.plugins.powerly.hilt)
}

android {
    namespace = "${MyProject.NAMESPACE}.charging"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
}