package by.krossovochkin.fiberyunofficial.applist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.applist.domain.AppListRepository
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData

class AppListRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi
): AppListRepository {

    override suspend fun getAppList(): List<FiberyAppData> {
        val schema = fiberyServiceApi.getSchema()
        return schema.first()
            .result.fiberyTypes
            .filter { it.meta.isDomain && it.name != "fibery/user" }
            .map {
                FiberyAppData(
                    name = it.name.substringBefore("/")
                )
            }
            .distinct()
    }
}
