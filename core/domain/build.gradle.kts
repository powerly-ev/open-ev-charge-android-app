import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
}

android {
    namespace = "${MyProject.NAMESPACE}.core.domain"
}

dependencies {
    api(projects.core.model)
    implementation(libs.paging.runtime.ktx)
}
