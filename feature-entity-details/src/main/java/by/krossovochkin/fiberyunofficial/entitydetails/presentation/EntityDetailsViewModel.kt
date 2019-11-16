package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityDetailsData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import kotlinx.coroutines.launch

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
        return entityData.schema.fields.flatMap { fieldSchema -> mapItem(fieldSchema, entityData) }
    }

    private fun mapItem(
        fieldSchema: FiberyFieldSchema,
        entityData: FiberyEntityDetailsData
    ): List<ListItem> {
        return when (fieldSchema.type) {
            FiberyApiConstants.FieldType.TEXT.value -> mapTextItem(fieldSchema, entityData)
            else -> emptyList()
        }
    }

    private fun mapTextItem(
        fieldSchema: FiberyFieldSchema,
        entityData: FiberyEntityDetailsData
    ): List<ListItem> {
        return listOf(
            FieldTextItem(
                text = entityData.data[fieldSchema.name] as String
            )
        )
    }

    interface ParentListener
}

data class FieldTextItem(
    val text: String
) : ListItem
