import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.koin)
    alias(libs.plugins.powerly.serialization)
    alias(libs.plugins.powerly.test)
}

android {
    namespace = "${MyProject.NAMESPACE}.core.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(libs.paging.runtime.ktx)
    implementation(libs.ktor.client.core)
}