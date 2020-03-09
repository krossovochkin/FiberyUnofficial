package by.krossovochkin.fiberyunofficial.applist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.applist.domain.AppListRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData

class AppListRepositoryImpl(
    private val fiberyApiRepository: FiberyApiRepository
) : AppListRepository {

    override suspend fun getAppList(): List<FiberyAppData> {
        return fiberyApiRepository.getTypeSchemas()
            .filter { it.meta.isDomain && it.name != FiberyApiConstants.Type.USER.value }
            .map {
                FiberyAppData(
                    name = it.name.substringBefore(FiberyApiConstants.DELIMITER_APP_TYPE)
                )
            }
            .distinct()
    }
}
