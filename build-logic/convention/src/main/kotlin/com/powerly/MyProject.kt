package com.powerly

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object MyProject {
    const val NAMESPACE = "com.powerly"
    const val DISPLAY_NAME = "open-powerly"

    const val VERSION_CODE = 1
    const val VERSION_NAME = "0.1"

    const val COMPILE_SDK = 36
    const val TARGET_SDK = 35
    const val MIN_SDK = 24

    val javaVersion = JavaVersion.VERSION_18
    val jvmTarget = JvmTarget.JVM_18
}


