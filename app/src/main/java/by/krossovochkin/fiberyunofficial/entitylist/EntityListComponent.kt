package by.krossovochkin.fiberyunofficial.entitylist

import by.krossovochkin.fiberyunofficial.app.GlobalDependencies
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.entitylist.data.EntityListDataComponent
import by.krossovochkin.fiberyunofficial.entitylist.data.EntityListDataComponentFactory
import by.krossovochkin.fiberyunofficial.entitylist.domain.EntityListDomainComponentFactory
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListPresentationComponent
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListPresentationComponentFactory
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListViewModel
import dagger.Component

interface EntityListGlobalDependencies : GlobalDependencies

@Component(
    dependencies = [
        EntityListDataComponent::class,
        EntityListPresentationComponent::class
    ]
)
interface EntityListComponent {
    
    fun entityListViewModel(): EntityListViewModel
    
    fun inject(fragment: EntityListFragment)
}

object EntityListComponentFactory {

    fun create(
        fragment: EntityListFragment,
        fiberyEntityTypeSchema: FiberyEntityTypeSchema,
        entityListGlobalDependencies: EntityListGlobalDependencies
    ): EntityListComponent {
        val entityListDataComponent = EntityListDataComponentFactory
            .create(
                entityListGlobalDependencies = entityListGlobalDependencies
            )
        val entityListDomainComponent = EntityListDomainComponentFactory
            .create(
                dependencies = entityListDataComponent
            )
        val entityListPresentationComponent = EntityListPresentationComponentFactory
            .create(
                fragment = fragment,
                fiberyEntityTypeSchema = fiberyEntityTypeSchema,
                entityListDomainComponent = entityListDomainComponent,
                entityListGlobalDependencies = entityListGlobalDependencies
            )
        return DaggerEntityListComponent.builder()
            .entityListDataComponent(entityListDataComponent)
            .entityListPresentationComponent(entityListPresentationComponent)
            .build()
    }
}