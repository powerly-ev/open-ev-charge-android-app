import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.feature)
    alias(libs.plugins.powerly.koin)
    alias(libs.plugins.powerly.serialization)
    alias(libs.plugins.powerly.test)
}

android {
    namespace = "${MyProject.NAMESPACE}.user"
}

dependencies {
    implementation(projects.core.managers)
    implementation(projects.core.database)
    implementation(projects.core.data)
}