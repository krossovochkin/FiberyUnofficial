package by.krossovochkin.fiberyunofficial.pickersingleselect.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PickerSingleSelectViewModelFactory(
    private val args: PickerSingleSelectDialogFragment.Args
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == PickerSingleSelectViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            PickerSingleSelectViewModel(
                args
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
