import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.serialization)
}

android {
    namespace = "${MyProject.NAMESPACE}.navigation"
}

dependencies {
    api(projects.common.resources)
    api(projects.core.model)
    api(projects.core.domain)
    api(projects.core.network)
    api(projects.core.analytics)
    api(projects.core.database)

    api(platform(libs.kotlin.bom))
    api(libs.core.ktx)
    api(libs.work.runtime.ktx)
    api(libs.androidx.annotation)
    api(libs.kotlinx.datetime)

    implementation(libs.navigation.compose)
}
