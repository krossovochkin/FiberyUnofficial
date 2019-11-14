package by.krossovochkin.fiberyunofficial.entitytypelist

import by.krossovochkin.fiberyunofficial.app.GlobalDependencies
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import by.krossovochkin.fiberyunofficial.entitytypelist.data.EntityTypeListDataComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.data.EntityTypeListDataComponentFactory
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.EntityTypeListDomainComponentFactory
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListPresentationComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListPresentationComponentFactory
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListViewModel
import dagger.Component

interface EntityTypeListGlobalDependencies : GlobalDependencies

@Component(
    dependencies = [
        EntityTypeListDataComponent::class,
        EntityTypeListPresentationComponent::class
    ]
)
interface EntityTypeListComponent {
    
    fun entityTypeListViewModel(): EntityTypeListViewModel
    
    fun inject(fragment: EntityTypeListFragment)
}

object EntityTypeListComponentFactory {

    fun create(
        fragment: EntityTypeListFragment,
        fiberyAppData: FiberyAppData,
        entityTypeListGlobalDependencies: EntityTypeListGlobalDependencies
    ): EntityTypeListComponent {
        val entityTypeListDataComponent = EntityTypeListDataComponentFactory
            .create(
                entityTypeListGlobalDependencies = entityTypeListGlobalDependencies
            )
        val entityTypeListDomainComponent = EntityTypeListDomainComponentFactory
            .create(
                dependencies = entityTypeListDataComponent
            )
        val entityTypeListPresentationComponent = EntityTypeListPresentationComponentFactory
            .create(
                fragment = fragment,
                fiberyAppData = fiberyAppData,
                entityTypeListDomainComponent = entityTypeListDomainComponent,
                entityTypeListGlobalDependencies = entityTypeListGlobalDependencies
            )
        return DaggerEntityTypeListComponent.builder()
            .entityTypeListDataComponent(entityTypeListDataComponent)
            .entityTypeListPresentationComponent(entityTypeListPresentationComponent)
            .build()
    }
}