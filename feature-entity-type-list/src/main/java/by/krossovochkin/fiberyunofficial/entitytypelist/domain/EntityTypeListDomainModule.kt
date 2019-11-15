package by.krossovochkin.fiberyunofficial.entitytypelist.domain

import dagger.Module
import dagger.Provides

@Module
object EntityTypeListDomainModule {

    @JvmStatic
    @Provides
    fun getEntityTypeListInteractor(
        entityTypeListRepository: EntityTypeListRepository
    ): GetEntityTypeListInteractor {
        return GetEntityTypeListInteractorImpl(
            entityTypeListRepository
        )
    }
}
