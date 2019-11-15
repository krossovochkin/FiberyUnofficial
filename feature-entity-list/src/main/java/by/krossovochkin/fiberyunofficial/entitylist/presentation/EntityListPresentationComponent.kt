package by.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.entitylist.EntityListArgs
import by.krossovochkin.fiberyunofficial.entitylist.EntityListGlobalDependencies
import by.krossovochkin.fiberyunofficial.entitylist.EntityListParentListener
import by.krossovochkin.fiberyunofficial.entitylist.domain.EntityListDomainComponent
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(
    modules = [EntityListPresentationModule::class],
    dependencies = [EntityListDomainComponent::class, EntityListGlobalDependencies::class]
)
interface EntityListPresentationComponent {

    fun entityListViewModel(): EntityListViewModel

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        @BindsInstance
        fun entityListArgs(entityListArgs: EntityListArgs): Builder

        fun entityListDomainComponent(entityListDomainComponent: EntityListDomainComponent): Builder

        fun entityListPresentationModule(entityListPresentationModule: EntityListPresentationModule): Builder

        fun entityListGlobalDependencies(entityListGlobalDependencies: EntityListGlobalDependencies): Builder

        fun build(): EntityListPresentationComponent
    }
}

@Module
object EntityListPresentationModule {

    @JvmStatic
    @Provides
    fun entityListViewModel(
        fragment: Fragment,
        entityListViewModelFactory: EntityListViewModelFactory
    ): EntityListViewModel {
        return ViewModelProviders
            .of(fragment, entityListViewModelFactory)
            .get()
    }

    @JvmStatic
    @Provides
    fun entityListViewModelFactory(
        getEntityListInteractor: GetEntityListInteractor,
        entityListParentListener: EntityListParentListener,
        entityListArgs: EntityListArgs
    ): EntityListViewModelFactory {
        return EntityListViewModelFactory(
            getEntityListInteractor,
            entityListParentListener,
            entityListArgs
        )
    }
}

object EntityListPresentationComponentFactory {

    fun create(
        fragment: Fragment,
        entityListArgs: EntityListArgs,
        entityListDomainComponent: EntityListDomainComponent,
        entityListGlobalDependencies: EntityListGlobalDependencies
    ): EntityListPresentationComponent {
        return DaggerEntityListPresentationComponent.builder()
            .entityListDomainComponent(entityListDomainComponent)
            .entityListPresentationModule(EntityListPresentationModule)
            .entityListGlobalDependencies(entityListGlobalDependencies)
            .fragment(fragment)
            .entityListArgs(entityListArgs)
            .build()
    }
}