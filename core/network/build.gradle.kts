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
    api(projects.core.domain)
    implementation(projects.core.database)
    implementation(libs.kotlinx.coroutines)

    api(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
}