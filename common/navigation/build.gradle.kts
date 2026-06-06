import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.serialization)
}

android {
    namespace = "${MyProject.NAMESPACE}.navigation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.navigation.compose)
}
