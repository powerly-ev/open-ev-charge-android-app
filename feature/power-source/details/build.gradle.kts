import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.feature)
    alias(libs.plugins.powerly.koin)
    alias(libs.plugins.powerly.test)
}

android {
    namespace = "${MyProject.NAMESPACE}.powersource.details"
}

dependencies {
    implementation(projects.core.managers)
    implementation(projects.feature.powerSource.charging)
}
