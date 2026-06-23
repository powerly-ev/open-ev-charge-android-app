import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.feature)
    alias(libs.plugins.powerly.koin)
    alias(libs.plugins.powerly.serialization)
}
android {
    namespace = "${MyProject.NAMESPACE}.payment"
}

dependencies {
    implementation(projects.core.managers)
    implementation(projects.core.data)
    // Strip payment
    implementation(libs.stripe.android) {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk15to18")
    }
    implementation(libs.stripe.card.scan)
}