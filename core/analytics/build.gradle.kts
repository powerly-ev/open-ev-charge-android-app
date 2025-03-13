import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
}


android {
    namespace = "${MyProject.NAMESPACE}.core.analytics"
}
