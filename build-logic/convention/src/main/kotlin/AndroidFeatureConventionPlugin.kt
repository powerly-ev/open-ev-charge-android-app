import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Umbrella convention plugin for feature-style modules.
 *
 * Applies:
 *  - `powerly.library` — standard Android library setup
 *  - `powerly.compose` — Compose compiler + Koin Compose deps
 *
 * Bundles the foundational dependencies every feature needs:
 *  - `common/navigation`, `common/resources`, `common/ui`
 *  - `core/model`, `core/domain`, `core/network`
 *
 * On-demand modules (`core/database`, `core/analytics`, `core/managers`) are NOT
 * bundled — features that need them declare those explicitly so the dependency graph
 * reflects real usage.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("powerly.library")
                apply("powerly.compose")
            }
            dependencies {
                add("implementation", project(":common:navigation"))
                add("implementation", project(":common:resources"))
                add("implementation", project(":common:ui"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:network"))
            }
        }
    }
}
