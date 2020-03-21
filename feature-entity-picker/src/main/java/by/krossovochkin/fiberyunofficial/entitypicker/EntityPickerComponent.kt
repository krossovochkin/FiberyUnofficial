package by.krossovochkin.fiberyunofficial.entitypicker

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.entitypicker.data.EntityPickerDataModule
import by.krossovochkin.fiberyunofficial.entitypicker.domain.EntityPickerDomainModule
import by.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragment
import by.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerPresentationModule
import by.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface EntityPickerParentComponent : GlobalDependencies {

    fun entityPickerArgsProvider(): EntityPickerFragment.ArgsProvider
}

@EntityPicker
@Component(
    modules = [
        EntityPickerDataModule::class,
        EntityPickerDomainModule::class,
        EntityPickerPresentationModule::class
    ],
    dependencies = [
        EntityPickerParentComponent::class
    ]
)
interface EntityPickerComponent {

    fun entityPickerViewModel(): EntityPickerViewModel

    fun inject(fragment: EntityPickerFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        fun entityPickerParentDependencies(entityPickerParentComponent: EntityPickerParentComponent): Builder

        fun build(): EntityPickerComponent
    }
}

@Scope
@Retention
annotation class EntityPicker
