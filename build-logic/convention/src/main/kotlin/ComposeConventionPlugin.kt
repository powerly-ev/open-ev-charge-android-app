import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.powerly.configureAndroidCompose
import com.powerly.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Pure Compose setup for both library and application modules.
 *
 * Applies the Kotlin Compose compiler plugin, configures whichever Android extension is
 * present (deferred via `withPlugin` so order doesn't matter), and bundles the Koin
 * Compose dependencies (BOM + androidx-compose + viewmodel + navigation).
 */
class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        pluginManager.withPlugin("com.android.library") {
            extensions.configure<LibraryExtension> { configureAndroidCompose(this) }
        }
        pluginManager.withPlugin("com.android.application") {
            extensions.configure<ApplicationExtension> { configureAndroidCompose(this) }
        }

        dependencies {
            add("implementation", platform(libs.findLibrary("koin-bom").get()))
            add("implementation", libs.findLibrary("koin-androidx-compose").get())
            add("implementation", libs.findLibrary("koin-compose-viewmodel").get())
            add("implementation", libs.findLibrary("koin-compose-viewmodel-navigation").get())
        }
    }
}
