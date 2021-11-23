package com.krossovochkin.fiberyunofficial.applist.domain

import com.krossovochkin.fiberyunofficial.domain.FiberyAppData

class TestAppListRepository : AppListRepository {

    var appList: List<FiberyAppData> = emptyList()

    override suspend fun getAppList(): List<FiberyAppData> {
        return appList
    }
}
