import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.feature)
    alias(libs.plugins.powerly.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.powersource.charging"
}

dependencies {
    implementation(projects.core.managers)
    implementation(projects.core.data)
}
