package by.krossovochkin.fiberyunofficial.entitydetails

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.entitydetails.data.EntityDetailsDataModule
import by.krossovochkin.fiberyunofficial.entitydetails.domain.EntityDetailsDomainModule
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsPresentationModule
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface EntityDetailsParentComponent : GlobalDependencies {

    fun entityDetailsParentListener(): EntityDetailsViewModel.ParentListener

    fun entityDetailsArgsProvider(): EntityDetailsFragment.ArgsProvider
}

@EntityDetails
@Component(
    modules = [
        EntityDetailsDataModule::class,
        EntityDetailsDomainModule::class,
        EntityDetailsPresentationModule::class
    ],
    dependencies = [
        EntityDetailsParentComponent::class
    ]
)
interface EntityDetailsComponent {

    fun entityDetailsViewModel(): EntityDetailsViewModel

    fun inject(fragment: EntityDetailsFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        fun entityDetailsParentComponent(entityDetailsParentComponent: EntityDetailsParentComponent): Builder

        fun build(): EntityDetailsComponent
    }
}

@Scope
@Retention
annotation class EntityDetails