package by.krossovochkin.fiberyunofficial.pickermultiselect.presentation

import androidx.lifecycle.ViewModel
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData

class PickerMultiSelectViewModel(
    private val args: PickerMultiSelectDialogFragment.Args
) : ViewModel() {

    val item: FieldData.MultiSelectFieldData
        get() = args.item

    val fieldSchema: FiberyFieldSchema
        get() = args.fieldSchema
}
