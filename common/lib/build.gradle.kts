import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.serialization)
    alias(libs.plugins.powerly.hilt)
}

android {
    namespace = "${MyProject.NAMESPACE}.lib"
}

dependencies {
    api(projects.common.resources)
    api(projects.core.model)
    api(projects.core.data)
    api(projects.core.network)
    api(projects.core.analytics)

    implementation(projects.core.analytics.impl)
    implementation(projects.core.database)

    //--------- core
    api(platform(libs.kotlin.bom))
    api(libs.core.ktx)
    api(libs.activity.ktx)
    api(libs.lifecycle.viewmodel.ktx)
    api(libs.work.runtime.ktx)
    api(libs.androidx.annotation)
    api(libs.kotlinx.datetime)
    implementation(libs.navigation.compose)

    gmsImplementation(libs.firebase.messaging)

    // Search places
    implementation(libs.google.places)
    implementation(libs.play.services.location)
}