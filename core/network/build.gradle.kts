import com.powerly.MyProject
import com.powerly.getPropertiesFileName

plugins {
    alias(libs.plugins.powerly.library)
    alias(libs.plugins.powerly.koin)
    alias(libs.plugins.secrets)
}


android {
    namespace = "${MyProject.NAMESPACE}.core.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "APP_VERSION", "\"${MyProject.VERSION_NAME}\"")
        buildConfigField("int", "APP_TYPE", "3")
    }

    buildTypes {
        debug {
            buildConfigField("Boolean", "DEBUG", "true")
            buildConfigField("Boolean", "isTest", "true")
        }
        release {
            buildConfigField("Boolean", "DEBUG", "false")
            buildConfigField("Boolean", "isTest", "false")
        }
        getByName("preRelease") {
            initWith(getByName("release"))
            buildConfigField("Boolean", "DEBUG", "true")
            buildConfigField("Boolean", "isTest", "false")
        }
    }
}

secrets {
    defaultPropertiesFileName = "secrets.default.properties"
    val fileName = getPropertiesFileName(gradle)
    println("propertiesFileName $fileName")
    propertiesFileName = fileName
}


dependencies {
    implementation(projects.core.model)
    implementation(projects.core.database)
    implementation(libs.kotlinx.coroutines)
    //ktor
    implementation(libs.ktor.client.core)
    // Android engine (uses OkHttp under the hood)
    implementation(libs.ktor.client.okhttp)
    // JSON serialization (Kotlinx)
    implementation(libs.ktor.serialization.kotlinx.json)
    // Content negotiation plugin (to parse/serialize JSON automatically)
    implementation(libs.ktor.client.content.negotiation)
    // Logging (optional but very useful for debugging)
    implementation(libs.ktor.client.logging)
}