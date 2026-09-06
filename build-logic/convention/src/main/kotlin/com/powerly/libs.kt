package com.powerly

import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


internal val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun VersionCatalog.version(name: String): String = findVersion(name).get().toString()

// SDK levels
internal val VersionCatalog.compileSdk: Int get() = version("compileSdk").toInt()
internal val VersionCatalog.targetSdk: Int get() = version("targetSdk").toInt()
internal val VersionCatalog.minSdk: Int get() = version("minSdk").toInt()

// JVM target, derived from the single `jvmTarget` entry
internal fun VersionCatalog.javaVersion(): JavaVersion = JavaVersion.toVersion(version("jvmTarget"))
internal fun VersionCatalog.jvmTarget(): JvmTarget = JvmTarget.fromTarget(version("jvmTarget"))