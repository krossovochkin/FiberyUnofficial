package by.krossovochkin.fiberyunofficial.pickersingleselect.presentation

import androidx.lifecycle.ViewModel
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData

class PickerSingleSelectViewModel(
    private val args: PickerSingleSelectDialogFragment.Args
) : ViewModel() {

    val item: FieldData.SingleSelectFieldData
        get() = args.item

    val fieldSchema: FiberyFieldSchema
        get() = args.fieldSchema
}
