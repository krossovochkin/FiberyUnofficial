package by.krossovochkin.fiberyunofficial.entitycreate

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.entitycreate.data.EntityCreateDataModule
import by.krossovochkin.fiberyunofficial.entitycreate.domain.EntityCreateDomainModule
import by.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragment
import by.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreatePresentationModule
import by.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface EntityCreateParentComponent : GlobalDependencies {

    fun entityCreateArgsProvider(): EntityCreateFragment.ArgsProvider
}

@EntityCreate
@Component(
    modules = [
        EntityCreateDataModule::class,
        EntityCreateDomainModule::class,
        EntityCreatePresentationModule::class
    ],
    dependencies = [EntityCreateParentComponent::class]
)
interface EntityCreateComponent {

    fun entityCreateViewModel(): EntityCreateViewModel

    fun inject(fragment: EntityCreateFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        fun entityCreateGlobalDependencies(entityCreateParentComponent: EntityCreateParentComponent): Builder

        fun build(): EntityCreateComponent
    }
}

@Scope
@Retention
annotation class EntityCreate
