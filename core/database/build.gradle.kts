import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.room)
    alias(libs.plugins.powerly.koin)
    alias(libs.plugins.powerly.serialization)
}

android {
    namespace = "${MyProject.NAMESPACE}.core.database"
}

dependencies {
    implementation(projects.core.model)
}