import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.core.data"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.database)

    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.paging.runtime.ktx)
    implementation(libs.squareup.retrofit)
    api(libs.squareup.converter.gson)
}