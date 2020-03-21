package by.krossovochkin.fiberyunofficial.entitypicker.domain

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import dagger.Module
import dagger.Provides

@Module
object EntityPickerDomainModule {

    @JvmStatic
    @Provides
    fun getEntityListInteractor(
        entityPickerRepository: EntityPickerRepository
    ): GetEntityListInteractor {
        return GetEntityListInteractorImpl(entityPickerRepository)
    }

    @JvmStatic
    @Provides
    fun getEntityTypeInteractor(
        fiberyApiRepository: FiberyApiRepository
    ): GetEntityTypeSchemaInteractor {
        return GetEntityTypeSchemaInteractorImpl(fiberyApiRepository)
    }
}
