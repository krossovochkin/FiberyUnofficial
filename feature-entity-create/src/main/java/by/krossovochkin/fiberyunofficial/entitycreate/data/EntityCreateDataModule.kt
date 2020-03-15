package by.krossovochkin.fiberyunofficial.entitycreate.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.entitycreate.domain.EntityCreateRepository
import dagger.Module
import dagger.Provides

@Module
object EntityCreateDataModule {

    @JvmStatic
    @Provides
    fun entityCreateRepository(
        fiberyServiceApi: FiberyServiceApi,
        fiberyApiRepository: FiberyApiRepository
    ): EntityCreateRepository {
        return EntityCreateRepositoryImpl(fiberyServiceApi, fiberyApiRepository)
    }
}
