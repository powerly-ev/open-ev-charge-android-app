import com.powerly.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin that wires the shared unit-test stack onto a module's
 * `testImplementation` configuration:
 *
 * - JUnit4 — test runner / assertions
 * - MockK — Kotlin-first mocking
 * - Turbine — testing Kotlin `Flow`s
 * - kotlinx-coroutines-test — `runTest` and test dispatchers
 *
 * Apply it with `alias(libs.plugins.powerly.test)` so modules don't repeat the
 * same four dependency declarations.
 */
class UnitTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("testImplementation", libs.findLibrary("junit").get())
                add("testImplementation", libs.findLibrary("mockk").get())
                add("testImplementation", libs.findLibrary("turbine").get())
                add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
            }
        }
    }
}
