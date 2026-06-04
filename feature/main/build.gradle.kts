import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.feature)
    alias(libs.plugins.powerly.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.home"
}

dependencies {
    implementation(projects.feature.main.home)
    implementation(projects.feature.main.scan)
    implementation(projects.feature.main.orders)
    implementation(projects.feature.main.account)
}