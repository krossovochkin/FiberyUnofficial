package by.krossovochkin.fiberyunofficial.applist.presentation

import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem

data class AppListItem(
    val appData: FiberyAppData,
    val title: String
) : ListItem
