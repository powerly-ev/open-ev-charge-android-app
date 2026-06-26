package com.powerly.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit rule that replaces `Dispatchers.Main` with a [TestDispatcher] for the
 * duration of a test, so code that launches in `viewModelScope` works under a
 * plain JVM unit test.
 *
 * Defaults to [UnconfinedTestDispatcher], which runs launched coroutines eagerly
 * (no manual `advanceUntilIdle()`). Pass a `StandardTestDispatcher` when a test
 * needs to control virtual time.
 *
 * Usage:
 * ```
 * @get:Rule val mainDispatcherRule = MainDispatcherRule()
 * ```
 */
class MainDispatcherRule(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
