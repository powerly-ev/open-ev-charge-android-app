import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.compose)
    alias(libs.plugins.powerly.koin)
}
android {
    namespace = "${MyProject.NAMESPACE}.payment"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
    // Strip payment
    implementation(libs.stripe.android) {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk15to18")
    }
    implementation(libs.stripe.card.scan)
}