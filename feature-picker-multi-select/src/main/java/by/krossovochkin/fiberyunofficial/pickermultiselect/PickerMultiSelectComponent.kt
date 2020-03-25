package by.krossovochkin.fiberyunofficial.pickermultiselect

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.pickermultiselect.presentation.PickerMultiSelectDialogFragment
import by.krossovochkin.fiberyunofficial.pickermultiselect.presentation.PickerMultiSelectPresentationModule
import by.krossovochkin.fiberyunofficial.pickermultiselect.presentation.PickerMultiSelectViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface PickerMultiSelectParentComponent : GlobalDependencies {

    fun pickerMultiSelectArgsProvider(): PickerMultiSelectDialogFragment.ArgsProvider
}

@PickerMultiSelect
@Component(
    modules = [
        PickerMultiSelectPresentationModule::class
    ],
    dependencies = [
        PickerMultiSelectParentComponent::class
    ]
)
interface PickerMultiSelectComponent {

    fun pickerMultiSelectViewModel(): PickerMultiSelectViewModel

    fun inject(fragment: PickerMultiSelectDialogFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        fun pickerMultiSelectParentComponent(
            pickerMultiSelectParentComponent: PickerMultiSelectParentComponent
        ): Builder

        fun build(): PickerMultiSelectComponent
    }
}

@Scope
@Retention
annotation class PickerMultiSelect
