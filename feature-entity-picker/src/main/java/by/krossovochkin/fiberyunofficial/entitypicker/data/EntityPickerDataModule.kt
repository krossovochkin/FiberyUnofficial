package by.krossovochkin.fiberyunofficial.entitypicker.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.entitypicker.domain.EntityPickerRepository
import dagger.Module
import dagger.Provides

@Module
object EntityPickerDataModule {

    @JvmStatic
    @Provides
    fun entityPickerRepository(
        fiberyServiceApi: FiberyServiceApi,
        fiberyApiRepository: FiberyApiRepository
    ): EntityPickerRepository {
        return EntityPickerRepositoryImpl(
            fiberyServiceApi = fiberyServiceApi,
            fiberyApiRepository = fiberyApiRepository
        )
    }
}
