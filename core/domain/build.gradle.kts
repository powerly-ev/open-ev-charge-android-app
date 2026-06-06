import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.serialization)
}

android {
    namespace = "${MyProject.NAMESPACE}.core.domain"
}

dependencies {
    implementation(libs.paging.runtime.ktx)
}
