pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "open-powerly-android"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")

// features
include(":feature:home")
include(":feature:home:map")
include(":feature:home:scan")
include(":feature:home:orders")
include(":feature:home:account")

include(":feature:power-source")
include(":feature:power-source:charge")
include(":feature:vehicles")

include(":feature:splash")
include(":feature:user")
include(":feature:payment")

// common
include(":common:testing")
include(":common:lib")
include(":common:ui")
include(":common:resources")

// core
include(":core:data")
include(":core:model")
include(":core:network")
include(":core:database")
include(":core:analytics")
include(":core:analytics:impl")
include(":benchmark")
