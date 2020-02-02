package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityDetailsData
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class EntityDetailsViewModel(
    private val getEntityDetailsInteractor: GetEntityDetailsInteractor,
    private val entityDetailsParentListener: ParentListener,
    private val entityDetailsArgs: EntityDetailsFragment.Args
) : ViewModel() {

    private val mutableEntityDetailsItems = MutableLiveData<List<ListItem>>()
    val items: LiveData<List<ListItem>> = mutableEntityDetailsItems

    init {
        viewModelScope.launch {
            val entityData = getEntityDetailsInteractor.execute(entityDetailsArgs.entityData)

            mutableEntityDetailsItems.value = mapItems(entityData)
        }
    }

    fun selectEntity(entityData: FiberyEntityData) {
        entityDetailsParentListener.onEntitySelected(entityData)
    }

    private fun mapItems(entityData: FiberyEntityDetailsData): List<ListItem> {
        val fields = entityData.fields.flatMap { field ->
            mapItem(field)
        }

        return listOf(
            FieldHeaderItem(
                publicId = entityData.publicId,
                title = entityData.title
            )
        ) + fields
    }

    private fun mapItem(
        field: FieldData
    ): List<ListItem> {
        return when (field) {
            is FieldData.TextFieldData -> mapTextItem(field)
            is FieldData.NumberFieldData -> mapNumberItem(field)
            is FieldData.DateTimeFieldData -> mapDateTimeItem(field)
            is FieldData.SingleSelectFieldData -> mapSingleSelectItem(field)
            is FieldData.RichTextFieldData -> mapRichTextItem(field)
            is FieldData.RelationFieldData -> mapRelationItem(field)
            is FieldData.CollectionFieldData -> mapCollectionItem(field)
        }
    }

    private fun mapTextItem(
        field: FieldData.TextFieldData
    ): List<ListItem> {
        return listOf(
            FieldTextItem(
                title = field.title,
                text = field.value
            )
        )
    }

    private fun mapDateTimeItem(
        field: FieldData.DateTimeFieldData
    ): List<ListItem> {
        return listOf(
            FieldTextItem(
                title = field.title,
                text = SimpleDateFormat.getInstance().format(field.value)
            )
        )
    }

    private fun mapNumberItem(
        field: FieldData.NumberFieldData
    ): List<ListItem> {
        return listOf(
            FieldTextItem(
                title = field.title,
                text = DecimalFormat.getInstance().format(field.value)
            )
        )
    }

    private fun mapSingleSelectItem(
        field: FieldData.SingleSelectFieldData
    ): List<ListItem> {
        return listOf(
            FieldTextItem(
                title = field.title,
                text = field.value
            )
        )
    }

    private fun mapRichTextItem(
        field: FieldData.RichTextFieldData
    ): List<ListItem> {
        return listOf(
            FieldRichTextItem(
                title = field.title,
                value = field.value
            )
        )
    }

    private fun mapRelationItem(field: FieldData.RelationFieldData): List<ListItem> {
        return listOf(
            FieldRelationItem(
                title = field.title,
                entityName = field.fiberyEntityData.title,
                entityData = field.fiberyEntityData
            )
        )
    }

    private fun mapCollectionItem(field: FieldData.CollectionFieldData): List<ListItem> {
        return listOf(
            FieldTextItem(
                title = field.title,
                text = field.value
            )
        )
    }

    interface ParentListener {

        fun onEntitySelected(entity: FiberyEntityData)
    }
}

data class FieldHeaderItem(
    val publicId: String,
    val title: String
) : ListItem

data class FieldTextItem(
    val title: String,
    val text: String
) : ListItem

data class FieldRichTextItem(
    val title: String,
    val value: String
) : ListItem

data class FieldRelationItem(
    val title: String,
    val entityName: String,
    val entityData: FiberyEntityData
) : ListItem
