package by.krossovochkin.fiberyunofficial.entitytypelist

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.entitytypelist.data.EntityTypeListDataModule
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.EntityTypeListDomainModule
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListPresentationModule
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface EntityTypeListParentComponent : GlobalDependencies {

    fun entityTypeListParentListener(): EntityTypeListViewModel.ParentListener

    fun entityTypeListArgsProvider(): EntityTypeListFragment.ArgsProvider
}

@EntityTypeList
@Component(
    modules = [
        EntityTypeListDataModule::class,
        EntityTypeListDomainModule::class,
        EntityTypeListPresentationModule::class
    ],
    dependencies = [
        EntityTypeListParentComponent::class
    ]
)
interface EntityTypeListComponent {

    fun entityTypeListViewModel(): EntityTypeListViewModel

    fun inject(fragment: EntityTypeListFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        fun entityTypeListGlobalDependencies(entityTypeListParentComponent: EntityTypeListParentComponent): Builder

        fun build(): EntityTypeListComponent
    }
}

@Scope
@Retention
annotation class EntityTypeList