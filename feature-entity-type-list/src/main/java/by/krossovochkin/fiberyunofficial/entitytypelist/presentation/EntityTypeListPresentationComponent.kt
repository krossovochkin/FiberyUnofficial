package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListArgs
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListGlobalDependencies
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListParentListener
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.EntityTypeListDomainComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.GetEntityTypeListInteractor
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(
    modules = [EntityTypeListPresentationModule::class],
    dependencies = [EntityTypeListDomainComponent::class, EntityTypeListGlobalDependencies::class]
)
interface EntityTypeListPresentationComponent {

    fun entityTypeListViewModel(): EntityTypeListViewModel

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        @BindsInstance
        fun args(args: EntityTypeListArgs): Builder

        fun entityTypeListDomainComponent(entityTypeListDomainComponent: EntityTypeListDomainComponent): Builder

        fun entityTypeListPresentationModule(entityTypeListPresentationModule: EntityTypeListPresentationModule): Builder

        fun entityTypeListGlobalDependencies(entityTypeListGlobalDependencies: EntityTypeListGlobalDependencies): Builder

        fun build(): EntityTypeListPresentationComponent
    }
}

@Module
object EntityTypeListPresentationModule {

    @JvmStatic
    @Provides
    fun entityTypeListViewModel(
        fragment: Fragment,
        entityTypeListViewModelFactory: EntityTypeListViewModelFactory
    ): EntityTypeListViewModel {
        return ViewModelProviders
            .of(fragment, entityTypeListViewModelFactory)
            .get()
    }

    @JvmStatic
    @Provides
    fun entityTypeListViewModelFactory(
        getEntityTypeListInteractor: GetEntityTypeListInteractor,
        entityTypeListParentListener: EntityTypeListParentListener,
        args: EntityTypeListArgs
    ): EntityTypeListViewModelFactory {
        return EntityTypeListViewModelFactory(
            getEntityTypeListInteractor,
            entityTypeListParentListener,
            args
        )
    }
}

object EntityTypeListPresentationComponentFactory {

    fun create(
        fragment: Fragment,
        args: EntityTypeListArgs,
        entityTypeListDomainComponent: EntityTypeListDomainComponent,
        entityTypeListGlobalDependencies: EntityTypeListGlobalDependencies
    ): EntityTypeListPresentationComponent {
        return DaggerEntityTypeListPresentationComponent.builder()
            .entityTypeListDomainComponent(entityTypeListDomainComponent)
            .entityTypeListPresentationModule(EntityTypeListPresentationModule)
            .entityTypeListGlobalDependencies(entityTypeListGlobalDependencies)
            .fragment(fragment)
            .args(args)
            .build()
    }
}