package com.krossovochkin.test.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalStdlibApi::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
fun runBlockingAndroidTest(block: TestScope.() -> Unit) {
    runTest(UnconfinedTestDispatcher()) {
        Dispatchers.setMain(this.coroutineContext[CoroutineDispatcher]!!)
        this.block()
        Dispatchers.resetMain()
    }
}

fun <T> Flow<T>.test(scope: TestScope): TestObserver<T> {
    return TestObserver(scope, this)
}
