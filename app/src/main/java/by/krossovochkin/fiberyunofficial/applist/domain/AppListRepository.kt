package by.krossovochkin.fiberyunofficial.applist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData

interface AppListRepository {

    suspend fun getAppList(): List<FiberyAppData>
}
