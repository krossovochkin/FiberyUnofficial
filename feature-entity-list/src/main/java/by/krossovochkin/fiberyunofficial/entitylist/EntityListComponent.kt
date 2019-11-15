package by.krossovochkin.fiberyunofficial.entitylist

import android.os.Bundle
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.entitylist.data.EntityListDataComponent
import by.krossovochkin.fiberyunofficial.entitylist.data.EntityListDataComponentFactory
import by.krossovochkin.fiberyunofficial.entitylist.domain.EntityListDomainComponentFactory
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListPresentationComponent
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListPresentationComponentFactory
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListViewModel
import dagger.Component

interface EntityListGlobalDependencies : GlobalDependencies {

    fun entityListParentListener(): EntityListParentListener

    fun entityListArgsProvider(): EntityListArgsProvider
}

interface EntityListParentListener {

    fun onEntitySelected(entity: FiberyEntityData)
}

interface EntityListArgsProvider {

    fun getEntityListArgs(arguments: Bundle): EntityListArgs
}

data class EntityListArgs(
    val entityTypeSchema: FiberyEntityTypeSchema
)

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
        val args = entityListGlobalDependencies
            .entityListArgsProvider()
            .getEntityListArgs(fragment.requireArguments())
        val entityListPresentationComponent = EntityListPresentationComponentFactory
            .create(
                fragment = fragment,
                entityListArgs = args,
                entityListDomainComponent = entityListDomainComponent,
                entityListGlobalDependencies = entityListGlobalDependencies
            )
        return DaggerEntityListComponent.builder()
            .entityListDataComponent(entityListDataComponent)
            .entityListPresentationComponent(entityListPresentationComponent)
            .build()
    }
}