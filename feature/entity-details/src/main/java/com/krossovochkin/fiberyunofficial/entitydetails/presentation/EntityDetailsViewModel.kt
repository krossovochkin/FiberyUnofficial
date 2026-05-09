/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the \"License\");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an \"AS IS\" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.krossovochkin.fiberyunofficial.entitydetails.presentation

import androidx.lifecycle.ViewModel
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarAction
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.core.presentation.viewmodel.load
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityDetailsData
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import com.krossovochkin.fiberyunofficial.entitydetails.domain.DeleteEntityInteractor
import com.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import com.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateEntityFieldInteractor
import com.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateMultiSelectFieldInteractor
import com.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateSingleSelectFieldInteractor
import com.krossovochkin.fiberyunofficial.navigation.EntityDetailsNavKey
import com.krossovochkin.fiberyunofficial.ui.list.ListItem
import com.krossovochkin.fiberyunofficial.ui.list.ListViewModelDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.text.DecimalFormat
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

import androidx.lifecycle.viewModelScope
import com.krossovochkin.core.presentation.result.ResultBus
import com.krossovochkin.fiberyunofficial.domain.MultiSelectPickedData
import com.krossovochkin.fiberyunofficial.domain.PickerEntityResultData
import com.krossovochkin.fiberyunofficial.domain.PickerSingleSelectResultData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EntityDetailsViewModel.Factory::class)
class EntityDetailsViewModel @AssistedInject constructor(
    private val getEntityDetailsInteractor: GetEntityDetailsInteractor,
    private val updateSingleSelectFieldInteractor: UpdateSingleSelectFieldInteractor,
    private val updateMultiSelectFieldInteractor: UpdateMultiSelectFieldInteractor,
    private val updateEntityFieldInteractor: UpdateEntityFieldInteractor,
    private val deleteEntityInteractor: DeleteEntityInteractor,
    private val resultBus: ResultBus,
    @Assisted private val entityDetailsArgs: EntityDetailsNavKey,
) : ViewModel() {

    init {
        viewModelScope.launch {
            resultBus.results.collect { result ->
                when (result) {
                    is PickerSingleSelectResultData -> {
                        updateSingleSelectField(result.fieldSchema, result.selectedValue)
                    }
                    is MultiSelectPickedData -> {
                        updateMultiSelectField(result)
                    }
                    is PickerEntityResultData -> {
                        updateEntityField(result.fieldSchema, result.entity)
                    }
                }
            }
        }
    }

    val entityData: FiberyEntityData
        get() = entityDetailsArgs.entity

    val progress = MutableStateFlow(false)
    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()

    private val navigationChannel = Channel<EntityDetailsNavigation>(Channel.BUFFERED)
    val navigation: Flow<EntityDetailsNavigation>
        get() = navigationChannel.receiveAsFlow()

    private val listDelegate = ListViewModelDelegate(
        viewModel = this,
        progress = progress,
        errorChannel = errorChannel,
        load = {
            mapItems(getEntityDetailsInteractor.execute(entityDetailsArgs.entity))
        }
    )

    val items = listDelegate.items

    val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = NativeText.Simple(
                "${entityDetailsArgs.entity.schema.displayName} #${entityDetailsArgs.entity.publicId}"
            ),
            bgColor = NativeColor.Hex(entityDetailsArgs.entity.schema.meta.uiColorHex),
            hasBackButton = true,
            actions = listOf(ToolbarAction.DELETE)
        )

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
            is FieldData.UrlFieldData -> mapUrlItem(field)
            is FieldData.EmailFieldData -> mapEmailItem(field)
            is FieldData.NumberFieldData -> mapNumberItem(field)
            is FieldData.DateFieldData -> mapDateItem(field)
            is FieldData.DateTimeFieldData -> mapDateTimeItem(field)
            is FieldData.DateRangeFieldData -> mapDateRangeItem(field)
            is FieldData.DateTimeRangeFieldData -> mapDateTimeRangeItem(field)
            is FieldData.SingleSelectFieldData -> mapSingleSelectItem(field)
            is FieldData.MultiSelectFieldData -> mapMultiSelectItem(field)
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

    private fun mapUrlItem(
        field: FieldData.UrlFieldData
    ): List<ListItem> {
        return listOf(
            FieldUrlItem(
                title = field.title,
                url = field.value.orEmpty()
            )
        )
    }

    private fun mapEmailItem(
        field: FieldData.EmailFieldData
    ): List<ListItem> {
        return listOf(
            FieldEmailItem(
                title = field.title,
                email = field.value.orEmpty()
            )
        )
    }

    private fun mapDateItem(
        field: FieldData.DateFieldData
    ): List<ListItem> {
        return listOf(
            FieldTextItem(
                title = field.title,
                text = field.value
                    ?.format(
                        DateTimeFormatter
                            .ofLocalizedDate(FormatStyle.MEDIUM)
                            .withZone(ZoneId.systemDefault())
                    )
                    .orEmpty()
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

    private fun mapDateRangeItem(
        field: FieldData.DateRangeFieldData
    ): List<ListItem> {
        val formatter = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM)
            .withZone(ZoneId.systemDefault())
        val formattedStart = field.start?.format(formatter).orEmpty()
        val formattedEnd = field.end?.format(formatter).orEmpty()
        return listOf(
            FieldTextItem(
                title = field.title,
                text = if (field.start != null && field.end != null) {
                    "$formattedStart — $formattedEnd"
                } else {
                    null
                }.orEmpty()

            )
        )
    }

    private fun mapDateTimeRangeItem(
        field: FieldData.DateTimeRangeFieldData
    ): List<ListItem> {
        val formatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withZone(ZoneId.systemDefault())
        val formattedStart = field.start?.format(formatter).orEmpty()
        val formattedEnd = field.end?.format(formatter).orEmpty()
        return listOf(
            FieldTextItem(
                title = field.title,
                text = if (field.start != null && field.end != null) {
                    "$formattedStart — $formattedEnd"
                } else {
                    null
                }.orEmpty()
            )
        )
    }

    private fun mapNumberItem(
        field: FieldData.NumberFieldData
    ): List<ListItem> {
        val formattedValue = field.value
            ?.let { value ->
                val format = if (field.precision != 0) {
                    ".".padEnd(field.precision + 1, '0')
                } else {
                    ""
                }
                DecimalFormat(format).format(value)
            }
            .orEmpty()
        return listOf(
            FieldTextItem(
                title = field.title,
                text = field.unit?.let { "$formattedValue $it" } ?: formattedValue
            )
        )
    }

    private fun mapSingleSelectItem(
        field: FieldData.SingleSelectFieldData
    ): List<ListItem> {
        return listOf(
            FieldSingleSelectItem(
                title = field.title,
                text = field.selectedValue?.title.orEmpty(),
                values = field.values,
                singleSelectData = field,
                fieldSchema = field.schema
            )
        )
    }

    private fun mapMultiSelectItem(
        field: FieldData.MultiSelectFieldData
    ): List<ListItem> {
        return listOf(
            FieldMultiSelectItem(
                title = field.title,
                text = field.selectedValues.joinToString(separator = ", ") { it.title },
                values = field.values,
                fieldSchema = field.schema,
                multiSelectData = field
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

    fun updateSingleSelectField(
        fieldSchema: FiberyFieldSchema,
        selectedValue: FieldData.EnumItemData?
    ) {
        if (selectedValue == null) {
            return
        }
        load(
            progress = progress,
            error = errorChannel
        ) {
            updateSingleSelectFieldInteractor.execute(
                parentEntityData = ParentEntityData(
                    fieldSchema = fieldSchema,
                    parentEntity = entityDetailsArgs.entity
                ),
                singleSelectItem = selectedValue
            )
            listDelegate.invalidate()
        }
    }

    fun updateMultiSelectField(data: MultiSelectPickedData) {
        load(
            progress = progress,
            error = errorChannel
        ) {
            updateMultiSelectFieldInteractor.execute(
                parentEntityData = ParentEntityData(
                    fieldSchema = data.fieldSchema,
                    parentEntity = entityDetailsArgs.entity
                ),
                addedItems = data.addedItems,
                removedItems = data.removedItems
            )
            listDelegate.invalidate()
        }
    }

    fun updateEntityField(fieldSchema: FiberyFieldSchema, entity: FiberyEntityData?) {
        load(
            progress = progress,
            error = errorChannel
        ) {
            updateEntityFieldInteractor.execute(
                parentEntityData = ParentEntityData(
                    fieldSchema = fieldSchema,
                    parentEntity = entityDetailsArgs.entity
                ),
                selectedEntity = entity
            )
            listDelegate.invalidate()
        }
    }

    fun deleteEntity() {
        load(
            progress = progress,
            error = errorChannel
        ) {
            deleteEntityInteractor.execute(entityDetailsArgs.entity)
            navigationChannel.send(EntityDetailsNavigation.Back)
        }
    }

    sealed class EntityDetailsNavigation {
        data object Back : EntityDetailsNavigation()
    }

    @AssistedFactory
    interface Factory {
        fun create(
            args: EntityDetailsNavKey,
        ): EntityDetailsViewModel
    }
}
