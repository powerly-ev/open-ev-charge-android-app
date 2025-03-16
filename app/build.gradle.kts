import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.powerly.MyProject
import com.powerly.appBuildName
import com.powerly.getPropertiesFileName
import com.powerly.isGoogle

plugins {
    alias(libs.plugins.powerly.application)
    alias(libs.plugins.powerly.hilt)
    alias(libs.plugins.powerly.application.compose)
    alias(libs.plugins.secrets)
}

if (isGoogle(gradle)) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
    apply(plugin = libs.plugins.firebase.crashlytics.get().pluginId)
}

android {
    namespace = MyProject.NAMESPACE
    compileSdk = MyProject.COMPILE_SDK

    defaultConfig {
        applicationId = MyProject.APPLICATION_ID
        versionCode = MyProject.VERSION_CODE
        versionName = MyProject.VERSION_NAME
        multiDexEnabled = true
    }

    buildFeatures {
        buildConfig = true
    }

    // set build output apk name ex: app-name-test-20-Mar.apk
    this.buildOutputs.all {
        appBuildName(this as BaseVariantOutputImpl)
    }

    buildTypes {
        // test version
        // debuggable && un-minified
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        // production version
        // un-debuggable & minified
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
        }
        // production-test
        // debuggable & un-minified && production server
        create("preRelease") {
            initWith(getByName("release"))
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks += listOf("release")
        }
    }

    packaging {
        jniLibs {
            keepDebugSymbols += listOf("*/mips/*.so", "*/mips64/*.so")
        }
        resources {
            merges += listOf("core.properties")
            excludes += listOf("META-INF/INDEX.LIST", "META-INF/io.netty.versions.properties")
        }
    }

    compileOptions {
        sourceCompatibility = MyProject.javaVersion
        targetCompatibility = MyProject.javaVersion
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "${MyProject.javaVersion}"
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

}

secrets {
    defaultPropertiesFileName = "secrets.default.properties"
    val fileName = getPropertiesFileName(gradle)
    propertiesFileName = fileName
}


dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
    implementation(projects.feature.splash)
    implementation(projects.feature.user)
    implementation(projects.feature.home)
    implementation(projects.feature.home.account)
    implementation(projects.feature.home.orders)
    implementation(projects.feature.powerSource)
    implementation(projects.feature.payment)
    implementation(projects.feature.vehicles)

    //--------- core
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.multidex)

    gmsImplementation(platform(libs.firebase.bom))
    gmsImplementation(libs.firebase.crashlytics)
}
