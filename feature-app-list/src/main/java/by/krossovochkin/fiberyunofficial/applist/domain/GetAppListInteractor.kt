package by.krossovochkin.fiberyunofficial.applist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData

interface GetAppListInteractor {

    suspend fun execute(): List<FiberyAppData>
}

class GetAppListInteractorImpl(
    private val appListRepository: AppListRepository
) : GetAppListInteractor {

    override suspend fun execute(): List<FiberyAppData> {
        return appListRepository.getAppList()
    }
}
