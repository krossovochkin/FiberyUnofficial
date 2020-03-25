package by.krossovochkin.fiberyunofficial.pickermultiselect.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.pickermultiselect.DaggerPickerMultiSelectComponent
import by.krossovochkin.fiberyunofficial.pickermultiselect.PickerMultiSelectParentComponent
import javax.inject.Inject

class PickerMultiSelectDialogFragment(
    private val pickerMultiSelectParentComponent: PickerMultiSelectParentComponent
) : DialogFragment() {

    @Inject
    lateinit var viewModel: PickerMultiSelectViewModel

    private var parentListener: ParentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerPickerMultiSelectComponent.builder()
            .fragment(this)
            .pickerMultiSelectParentComponent(pickerMultiSelectParentComponent)
            .build()
            .inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val fieldSchema = viewModel.fieldSchema
        val item = viewModel.item
        val selectedItemsIds = item.selectedValues.map { it.id }
        val selectedItemIds = item.values
            .mapIndexed { _, enumItemData -> enumItemData.id in selectedItemsIds }
            .toBooleanArray()
        return AlertDialog.Builder(requireContext())
            .setMultiChoiceItems(
                item.values.map { it.title }.toTypedArray(),
                selectedItemIds
            ) { _, index, isChecked ->
                selectedItemIds[index] = isChecked
            }
            .setTitle(item.title)
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                // TODO: pass via viewmodel
                val selectedItems = item.values.filterIndexed { index, _ -> selectedItemIds[index] }
                val addedItems = selectedItems.filter { value -> value !in item.selectedValues }
                val removedItems = item.selectedValues.filter { value -> value !in selectedItems }
                parentListener?.onMultiSelectPicked(
                    fieldSchema = fieldSchema,
                    addedItems = addedItems,
                    removedItems = removedItems
                )
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
        val item: FieldData.MultiSelectFieldData
    )

    interface ArgsProvider {

        fun getPickerMultiSelectArgs(arguments: Bundle): Args
    }

    interface ParentListener {

        fun onMultiSelectPicked(
            fieldSchema: FiberyFieldSchema,
            addedItems: List<FieldData.EnumItemData>,
            removedItems: List<FieldData.EnumItemData>
        )
    }
}
