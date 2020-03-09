package by.krossovochkin.fiberyunofficial.entitycreate.domain

import dagger.Module
import dagger.Provides

@Module
object EntityCreateDomainModule {

    @JvmStatic
    @Provides
    fun entityCreateInteractor(
        entityCreateRepository: EntityCreateRepository
    ): EntityCreateInteractor {
        return EntityCreateInteractorImpl(
            entityCreateRepository
        )
    }
}
