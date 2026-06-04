import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.serialization)
    alias(libs.plugins.powerly.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.core.managers"
}

dependencies {
    implementation(projects.common.resources)
    implementation(projects.common.navigation)
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.analytics)
    implementation(projects.core.analytics.impl)

    implementation(libs.core.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.kotlinx.datetime)

    implementation(libs.pusher.client)
    implementation(libs.google.places)
    implementation(libs.play.services.location)
    gmsImplementation(libs.firebase.messaging)
}
