import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.powerly.buildlogic"

// Configure the build-logic plugins to target JDK 18
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_18
    }
}


dependencies {
    compileOnly(libs.build.gradle)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApp") {
            id = "powerly.application"
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("androidLibrary") {
            id = "powerly.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("koin") {
            id = "powerly.koin"
            implementationClass = "KoinConventionPlugin"
        }
        register("compose") {
            id = "powerly.compose"
            implementationClass = "LibraryComposeConventionPlugin"
        }
        register("appCompose") {
            id = "powerly.application.compose"
            implementationClass = "AppComposeConventionPlugin"
        }
        register("androidTest") {
            id = "powerly.android.test"
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("serialization") {
            id = "powerly.serialization"
            implementationClass = "SerializationConventionPlugin"
        }
        register("androidRoom") {
            id = "powerly.room"
            implementationClass = "RoomConventionPlugin"
        }
    }
}
