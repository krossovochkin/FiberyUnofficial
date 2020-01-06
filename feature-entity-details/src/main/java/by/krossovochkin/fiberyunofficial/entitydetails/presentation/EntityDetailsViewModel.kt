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

    private fun mapItems(entityData: FiberyEntityDetailsData): List<ListItem> {
        val fields = entityData.fields.flatMap { field ->
            val fieldSchema = entityData.schema.fields.first { it.name == field.key }
            mapItem(fieldSchema, entityData)
        }

        return listOf(
            FieldHeaderItem(
                publicId = entityData.publicId,
                title = entityData.title
            )
        ) + fields
    }

    private fun mapItem(
        fieldSchema: FiberyFieldSchema,
        entityData: FiberyEntityDetailsData
    ): List<ListItem> {
        return when (fieldSchema.type) {
            FiberyApiConstants.FieldType.TEXT.value -> mapTextItem(fieldSchema, entityData)
            FiberyApiConstants.FieldType.DATE_TIME.value -> mapDateTimeItem(fieldSchema, entityData)
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
                title = fieldSchema.name.normalizeTitle(),
                text = entityData.fields[fieldSchema.name] as String
            )
        )
    }

    private fun mapDateTimeItem(
        fieldSchema: FiberyFieldSchema,
        entityData: FiberyEntityDetailsData
    ): List<ListItem> {
        return listOf(
            FieldTextItem(
                title = fieldSchema.name.normalizeTitle(),
                text = SimpleDateFormat.getInstance().format(
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(entityData.fields[fieldSchema.name] as String)!!
                )
            )
        )
    }

    private fun mapNumberItem(
        fieldSchema: FiberyFieldSchema,
        entityData: FiberyEntityDetailsData
    ): List<ListItem> {
        return listOf(
            FieldTextItem(
                title = fieldSchema.name.normalizeTitle(),
                text = DecimalFormat.getInstance().format(entityData.fields[fieldSchema.name].toString().toBigDecimal())
            )
        )
    }

    private fun String.normalizeTitle(): String {
        return this.substringAfter(DELIMITER_APP_TYPE)
            .split("-")
            .joinToString(separator = " ") { it.capitalize() }
    }

    interface ParentListener
}

data class FieldHeaderItem(
    val publicId: String,
    val title: String
) : ListItem

data class FieldTextItem(
    val title: String,
    val text: String
) : ListItem
