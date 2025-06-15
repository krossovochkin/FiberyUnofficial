package com.krossovochkin.fiberyunofficial.applist.domain

import com.google.common.truth.Truth.assertThat
import com.krossovochkin.fiberyfunofficial.test.domain.FiberyAppDataBuilder
import com.krossovochkin.fiberyunofficial.api.TestFiberyApiRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetAppListInteractorTest {

    private val api = TestFiberyApiRepository()

    private val interactor: GetAppListInteractor = GetAppListInteractor(
        fiberyApiRepository = api
    )

    @Test
    fun `returns list of apps`() = runTest {
        assertThat(interactor.execute())
            .isEqualTo(
                listOf(
                    FiberyAppDataBuilder().apply {
                        name = "Test App"
                    }.build(),
                    FiberyAppDataBuilder().apply {
                        name = "FiberyUnofficial"
                    }.build(),
                    FiberyAppDataBuilder().apply {
                        name = "KWeather"
                    }.build(),
                    FiberyAppDataBuilder().apply {
                        name = "Personal Website"
                    }.build(),
                    FiberyAppDataBuilder().apply {
                        name = "Self-Development"
                    }.build()
                )
            )
    }
}
