import com.powerly.MyProject

plugins {
    alias(libs.plugins.powerly.library)
}

android {
    namespace = "${MyProject.NAMESPACE}.testing"
}

// Shared unit-test dependencies are provided to each module by the `powerly.test`
// convention plugin (see build-logic). This module hosts shared test fixtures
// (rules, fakes, data builders) reused across module test suites.
