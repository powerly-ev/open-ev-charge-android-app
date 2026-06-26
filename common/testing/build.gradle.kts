import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
}

android {
    namespace = "${MyProject.NAMESPACE}.testing"
}

dependencies {
    // Exposed on the main classpath so shared fixtures (rules, fakes) compile here
    // and consumers get them transitively. Per-module test deps still come from the
    // `powerly.test` convention plugin.
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
}
