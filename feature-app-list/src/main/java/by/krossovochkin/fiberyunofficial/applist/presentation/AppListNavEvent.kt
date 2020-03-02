package by.krossovochkin.fiberyunofficial.applist.presentation

import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData

sealed class AppListNavEvent {

    data class OnAppSelectedEvent(
        val fiberyAppData: FiberyAppData
    ) : AppListNavEvent()
}
