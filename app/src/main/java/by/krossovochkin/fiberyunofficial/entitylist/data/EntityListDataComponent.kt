package by.krossovochkin.fiberyunofficial.entitylist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.entitylist.EntityListGlobalDependencies
import by.krossovochkin.fiberyunofficial.entitylist.domain.EntityListDomainComponentDependencies
import by.krossovochkin.fiberyunofficial.entitylist.domain.EntityListRepository
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(
    modules = [EntityListDataModule::class],
    dependencies = [EntityListGlobalDependencies::class]
)
interface EntityListDataComponent : EntityListDomainComponentDependencies

@Module
object EntityListDataModule {

    @JvmStatic
    @Provides
    fun entityListRepository(
        fiberyServiceApi: FiberyServiceApi
    ): EntityListRepository {
        return EntityListRepositoryImpl(fiberyServiceApi)
    }
}

object EntityListDataComponentFactory {

    fun create(
        entityListGlobalDependencies: EntityListGlobalDependencies
    ): EntityListDataComponent {
        return DaggerEntityListDataComponent.builder()
            .entityListGlobalDependencies(entityListGlobalDependencies)
            .build()
    }
}
