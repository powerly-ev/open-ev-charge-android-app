import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.serialization)
}

android {
    namespace = "${MyProject.NAMESPACE}.core.model"
}

dependencies {
    implementation(libs.paging.runtime.ktx)
    implementation(libs.androidx.annotation.experimental)
    api(libs.kotlinx.datetime)
}