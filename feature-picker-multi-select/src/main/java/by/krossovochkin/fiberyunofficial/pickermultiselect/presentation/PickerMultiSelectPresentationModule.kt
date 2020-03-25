package by.krossovochkin.fiberyunofficial.pickermultiselect.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import dagger.Module
import dagger.Provides

@Module
object PickerMultiSelectPresentationModule  {

    @JvmStatic
    @Provides
    fun pickerMultiSelectArgs(
        fragment: Fragment,
        pickerMultiSelectArgsProvider: PickerMultiSelectDialogFragment.ArgsProvider
    ): PickerMultiSelectDialogFragment.Args {
        return pickerMultiSelectArgsProvider.getPickerMultiSelectArgs(fragment.requireArguments())
    }

    @JvmStatic
    @Provides
    fun pickerMultiSelectViewModel(
        fragment: Fragment,
        pickerMultiSelectViewModelFactory: PickerMultiSelectViewModelFactory
    ): PickerMultiSelectViewModel {
        return ViewModelProvider(fragment, pickerMultiSelectViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun pickerMultiSelectViewModelFactory(
        args: PickerMultiSelectDialogFragment.Args
    ): PickerMultiSelectViewModelFactory {
        return PickerMultiSelectViewModelFactory(
            args
        )
    }
}
