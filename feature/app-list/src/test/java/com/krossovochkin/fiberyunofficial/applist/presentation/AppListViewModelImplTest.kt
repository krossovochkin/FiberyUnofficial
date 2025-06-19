package com.krossovochkin.fiberyunofficial.applist.presentation

import android.view.View
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.fiberyfunofficial.test.domain.FiberyAppDataBuilder
import com.krossovochkin.fiberyunofficial.applist.domain.GetAppListInteractor
import com.krossovochkin.fiberyunofficial.domain.FiberyAppData
import com.krossovochkin.test.core.TestObserver
import com.krossovochkin.test.core.runBlockingAndroidTest
import com.krossovochkin.test.core.test
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import org.junit.Test
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock

internal class AppListViewModelImplTest {

    private var appList: List<FiberyAppData> = emptyList()
    private var exception: Exception? = null

    private val interactor: GetAppListInteractor = mock {
        onBlocking { execute() } doSuspendableAnswer {
            delay(LOAD_TIME_MILLIS)
            if (exception != null) {
                throw exception!!
            }
            appList
        }
    }

    private val viewModel: AppListViewModel by lazy {
        AppListViewModel(
            getAppListInteractor = interactor
        )
    }

    @Test
    fun `if load successful and list is empty then emits empty list`() = runBlockingAndroidTest {
        appList = emptyList()
        val observers = TestObservers(viewModel, this)

        observers
            .assertProgress(count = 1, latestValue = true)
            .assertAppList(count = 1, latestValue = emptyList())
            .assertError(count = 0)
            .assertNavigation(count = 0)

        advanceTimeBy(LOAD_TIME_MILLIS)
        runCurrent()

        observers
            .assertProgress(count = 2, latestValue = false)
            .assertAppList(count = 1, latestValue = emptyList())
            .assertError(count = 0)
            .assertNavigation(count = 0)

        observers.finish()
    }

    @Test
    fun `if load successful and list not empty then emits loaded list`() = runBlockingAndroidTest {
        appList = APP_LIST_DATA
        val observers = TestObservers(viewModel, this)

        observers
            .assertProgress(count = 1, latestValue = true)
            .assertAppList(count = 1, latestValue = emptyList())
            .assertError(count = 0)
            .assertNavigation(count = 0)

        advanceTimeBy(LOAD_TIME_MILLIS)
        runCurrent()

        observers
            .assertProgress(count = 2, latestValue = false)
            .assertAppList(count = 2, latestValue = APP_LIST_ITEMS)
            .assertError(count = 0)
            .assertNavigation(count = 0)

        observers.finish()
    }

    @Test
    fun `if load error then emits error`() = runBlockingAndroidTest {
        exception = ERROR
        val observers = TestObservers(viewModel, this)

        observers
            .assertProgress(count = 1, latestValue = true)
            .assertAppList(count = 1, latestValue = emptyList())
            .assertError(count = 0)
            .assertNavigation(count = 0)

        advanceTimeBy(LOAD_TIME_MILLIS)
        runCurrent()

        observers
            .assertProgress(count = 2, latestValue = false)
            .assertAppList(count = 1, latestValue = emptyList())
            .assertError(count = 1, latestValue = ERROR)
            .assertNavigation(count = 0)

        observers.finish()
    }

    @Test
    fun `on item click sends app click event`() = runBlockingAndroidTest {
        appList = APP_LIST_DATA
        val observers = TestObservers(viewModel, this)

        observers
            .assertProgress(count = 1, latestValue = true)
            .assertAppList(count = 1, latestValue = emptyList())
            .assertError(count = 0)
            .assertNavigation(count = 0)

        advanceTimeBy(LOAD_TIME_MILLIS)
        runCurrent()

        observers
            .assertProgress(count = 2, latestValue = false)
            .assertAppList(count = 2, latestValue = APP_LIST_ITEMS)
            .assertError(count = 0)
            .assertNavigation(count = 0)

        viewModel.select(APP_LIST_ITEM, APP_LIST_ITEM_VIEW)

        observers
            .assertProgress(count = 2, latestValue = false)
            .assertAppList(count = 2, latestValue = APP_LIST_ITEMS)
            .assertError(count = 0)
            .assertNavigation(
                count = 1,
                latestValue = AppListNavEvent.OnAppSelectedEvent(
                    fiberyAppData = APP_DATA,
                    itemView = APP_LIST_ITEM_VIEW
                )
            )

        observers.finish()
    }

    private class TestObservers private constructor(
        val progress: TestObserver<Boolean>,
        val appList: TestObserver<List<ListItem>>,
        val error: TestObserver<Exception>,
        val navigation: TestObserver<AppListNavEvent>,
    ) {
        constructor(
            viewModel: AppListViewModel,
            scope: TestScope
        ) : this(
            viewModel.progress.test(scope),
            viewModel.appItems.test(scope),
            viewModel.error.test(scope),
            viewModel.navigation.test(scope)
        )

        fun assertProgress(count: Int, latestValue: Boolean? = null) = apply {
            progress
                .assertValueCount(count)
                .run {
                    if (latestValue != null) {
                        assertLatestValue(latestValue)
                    }
                }
        }

        fun assertAppList(count: Int, latestValue: List<ListItem>? = null) = apply {
            appList
                .assertValueCount(count)
                .run {
                    if (latestValue != null) {
                        assertLatestValue(latestValue)
                    }
                }
        }

        fun assertError(count: Int, latestValue: Exception? = null) = apply {
            error.assertValueCount(count)
                .run {
                    if (latestValue != null) {
                        assertLatestValue(latestValue)
                    }
                }
        }

        fun assertNavigation(count: Int, latestValue: AppListNavEvent? = null) = apply {
            navigation.assertValueCount(count)
                .run {
                    if (latestValue != null) {
                        assertLatestValue(latestValue)
                    }
                }
        }

        fun finish() {
            progress.finish()
            appList.finish()
            error.finish()
            navigation.finish()
        }
    }

    companion object {
        private const val LOAD_TIME_MILLIS = 1000L
        private val APP_DATA = FiberyAppDataBuilder().apply { name = "Test" }.build()
        private val APP_LIST_ITEM = AppListItem(
            appData = APP_DATA,
            title = APP_DATA.name
        )
        private val APP_LIST_DATA = listOf(APP_DATA)
        private val APP_LIST_ITEMS = listOf(APP_LIST_ITEM)
        private val ERROR = Exception("load error")
        private val APP_LIST_ITEM_VIEW: View = mock {}
    }
}
