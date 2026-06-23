import com.powerly.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.koin.compiler.plugin.KoinGradleExtension

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Koin Compiler Plugin (Kotlin compiler plugin) — replaces the legacy
            // koin-ksp-compiler / KSP processor for Koin annotation codegen.
            pluginManager.apply("io.insert-koin.compiler.plugin")

            // Compile-time DI safety verifies each module's graph in isolation, which can't
            // see this app's intentional cross-module definitions (e.g. a definition in one
            // module consuming a type provided by another). Disable it; the full graph is
            // assembled and validated at runtime when the app loads all modules.
            extensions.configure<KoinGradleExtension> {
                compileSafety.set(false)
            }

            dependencies {
                // Koin BOM
                add("implementation", platform(libs.findLibrary("koin-bom").get()))
                add("implementation", libs.findLibrary("koin-core").get())
                add("implementation", libs.findLibrary("koin-android").get())
                add("implementation", libs.findLibrary("koin-annotations").get())
            }
        }
    }
}
