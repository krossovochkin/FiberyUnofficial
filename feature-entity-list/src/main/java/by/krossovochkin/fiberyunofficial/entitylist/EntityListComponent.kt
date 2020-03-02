package by.krossovochkin.fiberyunofficial.entitylist

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.entitylist.data.EntityListDataModule
import by.krossovochkin.fiberyunofficial.entitylist.domain.EntityListDomainModule
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListPresentationModule
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface EntityListParentComponent : GlobalDependencies {

    fun entityListArgsProvider(): EntityListFragment.ArgsProvider
}

@EntityList
@Component(
    modules = [
        EntityListDataModule::class,
        EntityListDomainModule::class,
        EntityListPresentationModule::class
    ],
    dependencies = [
        EntityListParentComponent::class
    ]
)
interface EntityListComponent {

    fun entityListViewModel(): EntityListViewModel

    fun inject(fragment: EntityListFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        fun entityListGlobalDependencies(entityListParentComponent: EntityListParentComponent): Builder

        fun build(): EntityListComponent
    }
}

@Scope
@Retention
annotation class EntityList
