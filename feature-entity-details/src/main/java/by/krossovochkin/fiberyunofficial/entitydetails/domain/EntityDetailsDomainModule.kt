package by.krossovochkin.fiberyunofficial.entitydetails.domain

import dagger.Module
import dagger.Provides

@Module
object EntityDetailsDomainModule {

    @JvmStatic
    @Provides
    fun getEntityDetailsInteractor(
        entityDetailsRepository: EntityDetailsRepository
    ): GetEntityDetailsInteractor {
        return GetEntityDetailsInteractorImpl(entityDetailsRepository)
    }
}
