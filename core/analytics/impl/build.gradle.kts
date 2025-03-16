import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.hilt)
}


android {
    namespace = "${MyProject.NAMESPACE}.core.analyticsImpl"
}


dependencies {
    implementation(projects.core.analytics)
}