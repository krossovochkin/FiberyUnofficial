package by.krossovochkin.fiberyunofficial.entitylist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.entitylist.domain.EntityListRepository
import dagger.Module
import dagger.Provides

@Module
object EntityListDataModule {

    @JvmStatic
    @Provides
    fun entityListRepository(
        fiberyServiceApi: FiberyServiceApi
    ): EntityListRepository {
        return EntityListRepositoryImpl(fiberyServiceApi)
    }
}
