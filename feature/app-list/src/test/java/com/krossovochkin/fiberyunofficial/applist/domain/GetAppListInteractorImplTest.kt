package com.krossovochkin.fiberyunofficial.applist.domain

import com.google.common.truth.Truth.assertThat
import com.krossovochkin.fiberyfunofficial.test.domain.FiberyAppDataBuilder
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class GetAppListInteractorImplTest {

    private val repository = TestAppListRepository()

    private val interactor: GetAppListInteractor = GetAppListInteractorImpl(
        appListRepository = repository
    )

    @Test
    fun `if repository has no data then returns empty list`() = runBlockingTest {
        assertThat(repository.getAppList())
            .isEmpty()

        assertThat(interactor.execute())
            .isEmpty()
    }

    @Test
    fun `if repository has data then returns that data`() = runBlockingTest {
        val appList = listOf(
            FiberyAppDataBuilder().apply {
                name = "App 1"
            }.build(),
            FiberyAppDataBuilder().apply {
                name = "App 2"
            }.build()
        )
        repository.appList = appList

        assertThat(interactor.execute())
            .isEqualTo(appList)
    }
}
