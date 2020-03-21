package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityDetailsData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateEntityFieldInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateSingleSelectFieldInteractor
import kotlinx.coroutines.launch
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.text.DecimalFormat

class EntityDetailsViewModel(
    private val getEntityDetailsInteractor: GetEntityDetailsInteractor,
    private val updateSingleSelectFieldInteractor: UpdateSingleSelectFieldInteractor,
    private val updateEntityFieldInteractor: UpdateEntityFieldInteractor,
    private val entityDetailsArgs: EntityDetailsFragment.Args
) : ViewModel() {

    private val mutableEntityDetailsItems = MutableLiveData<List<ListItem>>()
    val items: LiveData<List<ListItem>> = mutableEntityDetailsItems

    private val mutableProgress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> = mutableProgress

    private val mutableError = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = mutableError

    private val mutableNavigation = MutableLiveData<Event<EntityDetailsNavEvent>>()
    val navigation: LiveData<Event<EntityDetailsNavEvent>> = mutableNavigation

    val toolbarViewState: EntityDetailsToolbarViewState
        get() = EntityDetailsToolbarViewState(
            title = "${entityDetailsArgs.entityData.schema.displayName} #${entityDetailsArgs.entityData.publicId}",
            bgColorInt = ColorUtils.getColor(entityDetailsArgs.entityData.schema.meta.uiColorHex)
        )

    init {
        viewModelScope.launch {
            load()
        }
    }

    private suspend fun load() {
        try {
            mutableProgress.value = true
            val entityData = getEntityDetailsInteractor.execute(entityDetailsArgs.entityData)

            mutableEntityDetailsItems.value = mapItems(entityData)
        } catch (e: Exception) {
            mutableError.value = Event(e)
        } finally {
            mutableProgress.value = false
        }
    }

    private fun mapItems(entityData: FiberyEntityDetailsData): List<ListItem> {
        val fields = entityData.fields.flatMap { field ->
            mapItem(field)
        }

        return listOf(FieldHeaderItem(title = entityData.title)) + fields
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
            is FieldData.CheckboxFieldData -> mapCheckboxItem(field)
        }
    }

    private fun mapTextItem(
        field: FieldData.TextFieldData
    ): List<ListItem> {
        return listOf(
            FieldTextItem(
                title = field.title,
                text = field.value.orEmpty()
            )
        )
    }

    private fun mapDateTimeItem(
        field: FieldData.DateTimeFieldData
    ): List<ListItem> {
        return listOf(
            FieldTextItem(
                title = field.title,
                text = field.value
                    ?.format(
                        DateTimeFormatter
                            .ofLocalizedDateTime(FormatStyle.MEDIUM)
                            .withZone(ZoneId.systemDefault())
                    )
                    .orEmpty()
            )
        )
    }

    private fun mapNumberItem(
        field: FieldData.NumberFieldData
    ): List<ListItem> {
        return listOf(
            FieldTextItem(
                title = field.title,
                text = field.value?.let { DecimalFormat.getInstance().format(it) }.orEmpty()
            )
        )
    }

    private fun mapSingleSelectItem(
        field: FieldData.SingleSelectFieldData
    ): List<ListItem> {
        return listOf(
            FieldSingleSelectItem(
                title = field.title,
                text = field.value.orEmpty(),
                values = field.values,
                fieldSchema = field.schema
            )
        )
    }

    private fun mapRichTextItem(
        field: FieldData.RichTextFieldData
    ): List<ListItem> {
        return listOf(
            FieldRichTextItem(
                title = field.title,
                value = field.value.orEmpty()
            )
        )
    }

    private fun mapRelationItem(field: FieldData.RelationFieldData): List<ListItem> {
        return listOf(
            FieldRelationItem(
                title = field.title,
                entityName = field.fiberyEntityData?.title.orEmpty(),
                entityData = field.fiberyEntityData,
                fieldSchema = field.schema
            )
        )
    }

    private fun mapCollectionItem(field: FieldData.CollectionFieldData): List<ListItem> {
        return listOf(
            FieldCollectionItem(
                title = field.title,
                countText = field.count.toString(),
                entityData = field.entityData,
                entityTypeSchema = field.entityTypeSchema,
                fieldSchema = field.schema
            )
        )
    }

    private fun mapCheckboxItem(field: FieldData.CheckboxFieldData): List<ListItem> {
        return listOf(
            FieldCheckboxItem(
                title = field.title,
                value = field.value ?: false
            )
        )
    }

    fun selectSingleSelectField(item: FieldSingleSelectItem) {
        mutableNavigation.value = Event(EntityDetailsNavEvent.OnSingleSelectSelectedEvent(item))
    }

    fun updateSingleSelectField(
        currentTitle: String,
        fieldSchema: FiberyFieldSchema,
        selectedValue: FieldData.SingleSelectItemData
    ) {
        if (selectedValue.title != currentTitle) {
            viewModelScope.launch {
                try {
                    mutableProgress.value = true
                    updateSingleSelectFieldInteractor.execute(
                        entityData = entityDetailsArgs.entityData,
                        fieldSchema = fieldSchema,
                        singleSelectItem = selectedValue
                    )
                    load()
                } catch (e: Exception) {
                    mutableError.value = Event(e)
                } finally {
                    mutableProgress.value = false
                }
            }
        }
    }

    fun selectEntityField(fieldSchema: FiberyFieldSchema, entityData: FiberyEntityData?) {
        mutableNavigation.value = Event(
            EntityDetailsNavEvent.OnEntityFieldEditEvent(fieldSchema, entityData)
        )
    }

    fun openEntity(entityData: FiberyEntityData) {
        mutableNavigation.value = Event(
            EntityDetailsNavEvent.OnEntitySelectedEvent(entityData)
        )
    }

    fun updateEntityField(fieldSchema: FiberyFieldSchema, entity: FiberyEntityData?) {
        viewModelScope.launch {
            try {
                mutableProgress.value = true
                updateEntityFieldInteractor.execute(
                    entityData = entityDetailsArgs.entityData,
                    fieldSchema = fieldSchema,
                    selectedEntity = entity
                )
                load()
            } catch (e: Exception) {
                mutableError.value = Event(e)
            } finally {
                mutableProgress.value = false
            }
        }
    }

    fun selectCollectionField(
        entityTypeSchema: FiberyEntityTypeSchema,
        entityData: FiberyEntityData,
        fieldSchema: FiberyFieldSchema
    ) {
        mutableNavigation.value = Event(
            EntityDetailsNavEvent.OnEntityTypeSelectedEvent(
                entityTypeSchema = entityTypeSchema,
                entity = entityData,
                fieldSchema = fieldSchema
            )
        )
    }

    fun onBackPressed() {
        mutableNavigation.value = Event(EntityDetailsNavEvent.BackEvent)
    }
}

data class FieldHeaderItem(
    val title: String
) : ListItem

data class FieldTextItem(
    val title: String,
    val text: String
) : ListItem

data class FieldSingleSelectItem(
    val title: String,
    val text: String,
    val values: List<FieldData.SingleSelectItemData>,
    val fieldSchema: FiberyFieldSchema
) : ListItem

data class FieldRichTextItem(
    val title: String,
    val value: String
) : ListItem

data class FieldRelationItem(
    val title: String,
    val entityName: String,
    val entityData: FiberyEntityData?,
    val fieldSchema: FiberyFieldSchema
) : ListItem {

    val isDeleteAvailable: Boolean = entityData != null

    val isOpenAvailable: Boolean = entityData != null
}

data class FieldCollectionItem(
    val title: String,
    val countText: String,
    val entityTypeSchema: FiberyEntityTypeSchema,
    val entityData: FiberyEntityData,
    val fieldSchema: FiberyFieldSchema
) : ListItem

data class FieldCheckboxItem(
    val title: String,
    val value: Boolean
) : ListItem
