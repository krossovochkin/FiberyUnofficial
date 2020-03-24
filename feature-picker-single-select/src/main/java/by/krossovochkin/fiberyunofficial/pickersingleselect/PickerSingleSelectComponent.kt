package by.krossovochkin.fiberyunofficial.pickersingleselect

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectDialogFragment
import by.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectPresentationModule
import by.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface PickerSingleSelectParentComponent : GlobalDependencies {

    fun pickerSingleSelectArgsProvider(): PickerSingleSelectDialogFragment.ArgsProvider
}

@PickerSingleSelect
@Component(
    modules = [
        PickerSingleSelectPresentationModule::class
    ],
    dependencies = [
        PickerSingleSelectParentComponent::class
    ]
)
interface PickerSingleSelectComponent {

    fun pickerSingleSelectViewModel(): PickerSingleSelectViewModel

    fun inject(fragment: PickerSingleSelectDialogFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        fun pickerSingleSelectParentComponent(
            pickerSingleSelectParentComponent: PickerSingleSelectParentComponent
        ): Builder

        fun build(): PickerSingleSelectComponent
    }
}

@Scope
@Retention
annotation class PickerSingleSelect
