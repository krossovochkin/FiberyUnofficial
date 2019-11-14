package by.krossovochkin.fiberyunofficial.entitylist.domain

import dagger.Component
import dagger.Module
import dagger.Provides

interface EntityListDomainComponentDependencies {

    fun entityListRepository(): EntityListRepository
}

@Component(
    modules = [EntityListDomainModule::class],
    dependencies = [EntityListDomainComponentDependencies::class]
)
interface EntityListDomainComponent {

    fun getEntityListInteractor(): GetEntityListInteractor

    @Component.Builder
    interface Builder {

        fun entityListDomainComponentDependencies(dependencies: EntityListDomainComponentDependencies): Builder

        fun entityListDomainModule(entityListDomainModule: EntityListDomainModule): Builder

        fun build(): EntityListDomainComponent
    }
}

@Module
object EntityListDomainModule {

    @JvmStatic
    @Provides
    fun getEntityListInteractor(
        entityListRepository: EntityListRepository
    ): GetEntityListInteractor {
        return GetEntityListInteractorImpl(entityListRepository)
    }
}

object EntityListDomainComponentFactory {

    fun create(
        dependencies: EntityListDomainComponentDependencies
    ): EntityListDomainComponent {
        return DaggerEntityListDomainComponent.builder()
            .entityListDomainComponentDependencies(dependencies)
            .entityListDomainModule(EntityListDomainModule)
            .build()
    }
}