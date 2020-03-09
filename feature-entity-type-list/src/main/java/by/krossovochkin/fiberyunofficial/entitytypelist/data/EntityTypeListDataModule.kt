package by.krossovochkin.fiberyunofficial.entitytypelist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.EntityTypeListRepository
import dagger.Module
import dagger.Provides

@Module
object EntityTypeListDataModule {

    @JvmStatic
    @Provides
    fun entityTypeListRepository(
        fiberyApiRepository: FiberyApiRepository
    ): EntityTypeListRepository {
        return EntityTypeListRepositoryImpl(fiberyApiRepository)
    }
}
