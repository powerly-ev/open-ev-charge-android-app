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
    //for network call
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.converter.gson)
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.logging.interceptor)
    implementation(libs.kotlinx.coroutines)
}