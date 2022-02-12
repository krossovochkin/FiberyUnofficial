package com.krossovochkin.test.core

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope

class TestObserver<T>(
    scope: TestScope,
    flow: Flow<T>
) {
    private val values = mutableListOf<T>()
    private val job: Job = scope.launch {
        flow.collect { values.add(it) }
    }

    fun assertNoValues(): TestObserver<T> = apply {
        assertThat(this.values)
            .isEmpty()
    }

    fun assertValues(vararg values: T): TestObserver<T> = apply {
        assertThat(this.values)
            .containsExactly(values)
            .inOrder()
    }

    fun assertValueCount(count: Int): TestObserver<T> = apply {
        assertThat(this.values)
            .hasSize(count)
    }

    fun assertLatestValue(value: T): TestObserver<T> = apply {
        assertThat(this.values.last())
            .isEqualTo(value)
    }

    fun finish() {
        job.cancel()
    }
}
