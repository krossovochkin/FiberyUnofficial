package by.krossovochkin.fiberyunofficial.pickermultiselect.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PickerMultiSelectViewModelFactory(
    private val args: PickerMultiSelectDialogFragment.Args
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == PickerMultiSelectViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            PickerMultiSelectViewModel(
                args
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
