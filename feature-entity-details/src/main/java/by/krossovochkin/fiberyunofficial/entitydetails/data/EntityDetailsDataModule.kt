package by.krossovochkin.fiberyunofficial.entitydetails.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.entitydetails.domain.EntityDetailsRepository
import dagger.Module
import dagger.Provides

@Module
object EntityDetailsDataModule {

    @JvmStatic
    @Provides
    fun entityDetailsRepository(
        fiberyServiceApi: FiberyServiceApi
    ): EntityDetailsRepository {
        return EntityDetailsRepositoryImpl(fiberyServiceApi)
    }
}