package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants.DELIMITER_APP_TYPE
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityDetailsData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import kotlinx.coroutines.launch
import java.math.BigDecimal

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

    private fun mapItems(entityData: FiberyEntityDetailsData): List<ListItem> {
        val fields = entityData.fields.flatMap { field ->
            val fieldSchema = entityData.schema.fields.first { it.name == field.key }
            mapItem(fieldSchema, entityData)
        }

        return listOf(FieldHeaderItem(
            publicId = entityData.publicId,
            title = entityData.title
        )) + fields
    }

    private fun mapItem(
        fieldSchema: FiberyFieldSchema,
        entityData: FiberyEntityDetailsData
    ): List<ListItem> {
        return when (fieldSchema.type) {
            FiberyApiConstants.FieldType.TEXT.value -> mapTextItem(fieldSchema, entityData)
            FiberyApiConstants.FieldType.NUMBER.value -> mapNumberItem(fieldSchema, entityData)
            else -> emptyList()
        }
    }

    private fun mapTextItem(
        fieldSchema: FiberyFieldSchema,
        entityData: FiberyEntityDetailsData
    ): List<ListItem> {
        return listOf(
            FieldTextItem(
                title = fieldSchema.name.substringAfter(DELIMITER_APP_TYPE),
                text = entityData.fields[fieldSchema.name] as String
            )
        )
    }

    private fun mapNumberItem(
        fieldSchema: FiberyFieldSchema,
        entityData: FiberyEntityDetailsData
    ): List<ListItem> {
        return listOf(
            FieldNumberItem(
                title = fieldSchema.name.substringAfter(DELIMITER_APP_TYPE),
                value = entityData.fields[fieldSchema.name].toString().toBigDecimal()
            )
        )
    }

    interface ParentListener
}

data class FieldHeaderItem(
    val publicId: String,
    val title: String
): ListItem

data class FieldTextItem(
    val title: String,
    val text: String
) : ListItem

data class FieldNumberItem(
    val title: String,
    val value: BigDecimal
) : ListItem
