package by.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.entitylist.EntityListGlobalDependencies
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
        fun entityTypeSchema(fiberyEntityTypeSchema: FiberyEntityTypeSchema): Builder

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
        navController: NavController,
        getEntityListInteractor: GetEntityListInteractor,
        entityTypeSchema: FiberyEntityTypeSchema
    ): EntityListViewModelFactory {
        return EntityListViewModelFactory(
            navController,
            getEntityListInteractor,
            entityTypeSchema
        )
    }

    @JvmStatic
    @Provides
    fun navController(fragment: Fragment) = fragment.findNavController()
}

object EntityListPresentationComponentFactory {

    fun create(
        fragment: Fragment,
        fiberyEntityTypeSchema: FiberyEntityTypeSchema,
        entityListDomainComponent: EntityListDomainComponent,
        entityListGlobalDependencies: EntityListGlobalDependencies
    ): EntityListPresentationComponent {
        return DaggerEntityListPresentationComponent.builder()
            .entityListDomainComponent(entityListDomainComponent)
            .entityListPresentationModule(EntityListPresentationModule)
            .entityListGlobalDependencies(entityListGlobalDependencies)
            .fragment(fragment)
            .entityTypeSchema(fiberyEntityTypeSchema)
            .build()
    }
}