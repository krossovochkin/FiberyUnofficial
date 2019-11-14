package by.krossovochkin.fiberyunofficial.entitytypelist.domain

import dagger.Component
import dagger.Module
import dagger.Provides

interface EntityTypeListDomainComponentDependencies {

    fun entityTypeListRepository(): EntityTypeListRepository
}

@Component(
    modules = [EntityTypeListDomainModule::class],
    dependencies = [EntityTypeListDomainComponentDependencies::class]
)
interface EntityTypeListDomainComponent {

    fun getEntityTypeListInteractor(): GetEntityTypeListInteractor

    @Component.Builder
    interface Builder {

        fun entityTypeListDomainComponentDependencies(dependencies: EntityTypeListDomainComponentDependencies): Builder

        fun entityTypeListDomainModule(entityTypeListDomainModule: EntityTypeListDomainModule): Builder

        fun build(): EntityTypeListDomainComponent
    }
}

@Module
object EntityTypeListDomainModule {

    @JvmStatic
    @Provides
    fun getEntityTypeListInteractor(
        entityTypeListRepository: EntityTypeListRepository
    ): GetEntityTypeListInteractor {
        return GetEntityTypeListInteractorImpl(entityTypeListRepository)
    }
}

object EntityTypeListDomainComponentFactory {

    fun create(
        dependencies: EntityTypeListDomainComponentDependencies
    ): EntityTypeListDomainComponent {
        return DaggerEntityTypeListDomainComponent.builder()
            .entityTypeListDomainComponentDependencies(dependencies)
            .entityTypeListDomainModule(EntityTypeListDomainModule)
            .build()
    }
}