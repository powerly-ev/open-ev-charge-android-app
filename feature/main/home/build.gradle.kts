import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.feature)
    alias(libs.plugins.powerly.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.map"
}

dependencies {
    implementation(projects.core.managers)
    implementation(projects.feature.powerSource.details)
}