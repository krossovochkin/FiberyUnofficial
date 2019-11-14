package by.krossovochkin.fiberyunofficial.entitytypelist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListGlobalDependencies
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.EntityTypeListDomainComponentDependencies
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.EntityTypeListRepository
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(
    modules = [EntityTypeListDataModule::class],
    dependencies = [EntityTypeListGlobalDependencies::class]
)
interface EntityTypeListDataComponent : EntityTypeListDomainComponentDependencies

@Module
object EntityTypeListDataModule {

    @JvmStatic
    @Provides
    fun entityTypeListRepository(
        fiberyServiceApi: FiberyServiceApi
    ): EntityTypeListRepository {
        return EntityTypeListRepositoryImpl(fiberyServiceApi)
    }
}

object EntityTypeListDataComponentFactory {

    fun create(
        entityTypeListGlobalDependencies: EntityTypeListGlobalDependencies
    ): EntityTypeListDataComponent {
        return DaggerEntityTypeListDataComponent.builder()
            .entityTypeListGlobalDependencies(entityTypeListGlobalDependencies)
            .build()
    }
}
