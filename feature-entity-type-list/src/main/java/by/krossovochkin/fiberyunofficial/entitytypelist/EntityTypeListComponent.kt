package by.krossovochkin.fiberyunofficial.entitytypelist

import android.os.Bundle
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import by.krossovochkin.fiberyunofficial.entitytypelist.data.EntityTypeListDataComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.data.EntityTypeListDataComponentFactory
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.EntityTypeListDomainComponentFactory
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListPresentationComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListPresentationComponentFactory
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListViewModel
import dagger.Component

interface EntityTypeListGlobalDependencies : GlobalDependencies {

    fun entityTypeListParentListener(): EntityTypeListParentListener

    fun entityTypeListArgsProvider(): EntityTypeListArgsProvider
}

interface EntityTypeListParentListener {

    fun onEntityTypeSelected(entityTypeSchema: FiberyEntityTypeSchema)
}

interface EntityTypeListArgsProvider {

    fun getEntityTypeListArgs(arguments: Bundle): EntityTypeListArgs
}

data class EntityTypeListArgs(
    val fiberyAppData: FiberyAppData
)

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
        val args = entityTypeListGlobalDependencies
            .entityTypeListArgsProvider()
            .getEntityTypeListArgs(fragment.requireArguments())
        val entityTypeListPresentationComponent = EntityTypeListPresentationComponentFactory
            .create(
                fragment = fragment,
                args = args,
                entityTypeListDomainComponent = entityTypeListDomainComponent,
                entityTypeListGlobalDependencies = entityTypeListGlobalDependencies
            )
        return DaggerEntityTypeListComponent.builder()
            .entityTypeListDataComponent(entityTypeListDataComponent)
            .entityTypeListPresentationComponent(entityTypeListPresentationComponent)
            .build()
    }
}