package by.krossovochkin.fiberyunofficial.pickersingleselect.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.pickersingleselect.DaggerPickerSingleSelectComponent
import by.krossovochkin.fiberyunofficial.pickersingleselect.PickerSingleSelectParentComponent
import javax.inject.Inject

class PickerSingleSelectDialogFragment(
    private val pickerSingleSelectParentComponent: PickerSingleSelectParentComponent
) : DialogFragment() {

    @Inject
    lateinit var viewModel: PickerSingleSelectViewModel

    private var parentListener: ParentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerPickerSingleSelectComponent.builder()
            .fragment(this)
            .pickerSingleSelectParentComponent(pickerSingleSelectParentComponent)
            .build()
            .inject(this)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val fieldSchema = viewModel.fieldSchema
        val item = viewModel.item
        var selectedIndex: Int = item.values.indexOf(item.selectedValue)
        return AlertDialog.Builder(requireContext())
            .setSingleChoiceItems(
                item.values.map { it.title }.toTypedArray(),
                selectedIndex
            ) { _, index -> selectedIndex = index }
            .setTitle(item.title)
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                parentListener?.onSingleSelectPicked(fieldSchema, item.values[selectedIndex])
            }
            .create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentListener = context as ParentListener
    }

    override fun onDetach() {
        super.onDetach()
        parentListener = null
    }

    data class Args(
        val fieldSchema: FiberyFieldSchema,
        val item: FieldData.SingleSelectFieldData
    )

    interface ArgsProvider {

        fun getPickerSingleSelectArgs(arguments: Bundle): Args
    }

    interface ParentListener {

        fun onSingleSelectPicked(fieldSchema: FiberyFieldSchema, item: FieldData.EnumItemData)

        fun onBackPressed()
    }
}
