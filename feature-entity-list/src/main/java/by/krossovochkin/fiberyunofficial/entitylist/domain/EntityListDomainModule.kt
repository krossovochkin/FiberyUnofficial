package by.krossovochkin.fiberyunofficial.entitylist.domain

import dagger.Module
import dagger.Provides

@Module
object EntityListDomainModule {

    @JvmStatic
    @Provides
    fun getEntityListInteractor(
        entityListRepository: EntityListRepository
    ): GetEntityListInteractor {
        return GetEntityListInteractorImpl(entityListRepository)
    }

    @JvmStatic
    @Provides
    fun setEntityListFilterInteractor(
        entityListRepository: EntityListRepository
    ): SetEntityListFilterInteractor {
        return SetEntityListFilterInteractorImpl(entityListRepository)
    }

    @JvmStatic
    @Provides
    fun setEntityListSortInteractor(
        entityListRepository: EntityListRepository
    ): SetEntityListSortInteractor {
        return SetEntityListSortInteractorImpl(entityListRepository)
    }
}
