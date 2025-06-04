import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
}

dependencies {
    implementation(libs.material)
}

android {
    namespace = "${MyProject.NAMESPACE}.resources"
}
