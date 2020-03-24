package by.krossovochkin.fiberyunofficial.pickersingleselect.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import dagger.Module
import dagger.Provides

@Module
object PickerSingleSelectPresentationModule {

    @JvmStatic
    @Provides
    fun pickerSingleSelectArgs(
        fragment: Fragment,
        pickerSingleSelectArgsProvider: PickerSingleSelectDialogFragment.ArgsProvider
    ): PickerSingleSelectDialogFragment.Args {
        return pickerSingleSelectArgsProvider.getPickerSingleSelectArgs(fragment.requireArguments())
    }

    @JvmStatic
    @Provides
    fun pickerSingleSelectViewModel(
        fragment: Fragment,
        pickerSingleSelectViewModelFactory: PickerSingleSelectViewModelFactory
    ): PickerSingleSelectViewModel {
        return ViewModelProvider(fragment, pickerSingleSelectViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun pickerSingleSelectViewModelFactory(
        args: PickerSingleSelectDialogFragment.Args
    ): PickerSingleSelectViewModelFactory {
        return PickerSingleSelectViewModelFactory(
            args
        )
    }
}
