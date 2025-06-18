/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.krossovochkin.fiberyunofficial.entitydetails.presentation

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.list.ListViewModelDelegate
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.core.presentation.viewmodel.load
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityDetailsData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import com.krossovochkin.fiberyunofficial.entitydetails.R
import com.krossovochkin.fiberyunofficial.entitydetails.domain.DeleteEntityInteractor
import com.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import com.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateEntityFieldInteractor
import com.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateMultiSelectFieldInteractor
import com.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateSingleSelectFieldInteractor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.text.DecimalFormat

class EntityDetailsViewModel @AssistedInject constructor(
    private val getEntityDetailsInteractor: GetEntityDetailsInteractor,
    private val updateSingleSelectFieldInteractor: UpdateSingleSelectFieldInteractor,
    private val updateMultiSelectFieldInteractor: UpdateMultiSelectFieldInteractor,
    private val updateEntityFieldInteractor: UpdateEntityFieldInteractor,
    private val deleteEntityInteractor: DeleteEntityInteractor,
    @Assisted private val entityDetailsArgs: EntityDetailsFragment.Args
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(entityDetailsArgs: EntityDetailsFragment.Args): EntityDetailsViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            entityDetailsArgs: EntityDetailsFragment.Args
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return assistedFactory.create(entityDetailsArgs) as T
            }
        }
    }

    val progress = MutableStateFlow(false)
    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()
    private val navigationChannel = Channel<EntityDetailsNavEvent>(Channel.BUFFERED)
    val navigation: Flow<EntityDetailsNavEvent>
        get() = navigationChannel.receiveAsFlow()

    private val listDelegate = ListViewModelDelegate(
        viewModel = this,
        progress = progress,
        errorChannel = errorChannel,
        load = {
            mapItems(getEntityDetailsInteractor.execute(entityDetailsArgs.entityData))
        }
    )

    val items = listDelegate.items

    val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = NativeText.Simple(
                "${entityDetailsArgs.entityData.schema.displayName} #${entityDetailsArgs.entityData.publicId}"
            ),
            bgColor = NativeColor.Hex(entityDetailsArgs.entityData.schema.meta.uiColorHex),
            menuResId = R.menu.entity_details_menu,
            hasBackButton = true
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

    fun selectSingleSelectField(item: FieldSingleSelectItem) {
        viewModelScope.launch {
            navigationChannel.send(
                EntityDetailsNavEvent.OnSingleSelectSelectedEvent(
                    parentEntityData = ParentEntityData(
                        fieldSchema = item.fieldSchema,
                        parentEntity = entityDetailsArgs.entityData
                    ),
                    singleSelectItem = item.singleSelectData
                )
            )
        }
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
                    parentEntity = entityDetailsArgs.entityData
                ),
                singleSelectItem = selectedValue
            )
            listDelegate.invalidate()
        }
    }

    fun selectMultiSelectField(item: FieldMultiSelectItem) {
        viewModelScope.launch {
            navigationChannel.send(
                EntityDetailsNavEvent.OnMultiSelectSelectedEvent(
                    parentEntityData = ParentEntityData(
                        fieldSchema = item.fieldSchema,
                        parentEntity = entityDetailsArgs.entityData
                    ),
                    multiSelectItem = item.multiSelectData
                )
            )
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
                    parentEntity = entityDetailsArgs.entityData
                ),
                addedItems = data.addedItems,
                removedItems = data.removedItems
            )
            listDelegate.invalidate()
        }
    }

    fun selectEntityField(
        fieldSchema: FiberyFieldSchema,
        entityData: FiberyEntityData?,
        itemView: View
    ) {
        viewModelScope.launch {
            navigationChannel.send(
                EntityDetailsNavEvent.OnEntityFieldEditEvent(
                    parentEntityData = ParentEntityData(
                        fieldSchema = fieldSchema,
                        parentEntity = entityDetailsArgs.entityData
                    ),
                    currentEntity = entityData,
                    itemView = itemView
                )
            )
        }
    }

    fun openEntity(entityData: FiberyEntityData, itemView: View) {
        viewModelScope.launch {
            navigationChannel.send(
                EntityDetailsNavEvent.OnEntitySelectedEvent(entityData, itemView)
            )
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
                    parentEntity = entityDetailsArgs.entityData
                ),
                selectedEntity = entity
            )
            listDelegate.invalidate()
        }
    }

    fun selectCollectionField(
        entityTypeSchema: FiberyEntityTypeSchema,
        fieldSchema: FiberyFieldSchema,
        itemView: View
    ) {
        viewModelScope.launch {
            navigationChannel.send(
                EntityDetailsNavEvent.OnEntityTypeSelectedEvent(
                    entityTypeSchema = entityTypeSchema,
                    parentEntityData = ParentEntityData(
                        fieldSchema = fieldSchema,
                        parentEntity = entityDetailsArgs.entityData
                    ),
                    itemView = itemView
                )
            )
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            navigationChannel.send(EntityDetailsNavEvent.BackEvent)
        }
    }

    fun selectUrl(item: FieldUrlItem) {
        viewModelScope.launch {
            navigationChannel.send(
                EntityDetailsNavEvent.OpenUrlEvent(url = item.url)
            )
        }
    }

    fun selectEmail(item: FieldEmailItem) {
        viewModelScope.launch {
            navigationChannel.send(
                EntityDetailsNavEvent.SendEmailEvent(email = item.email)
            )
        }
    }

    fun deleteEntity() {
        load(
            progress = progress,
            error = errorChannel
        ) {
            deleteEntityInteractor.execute(entityDetailsArgs.entityData)
            onBackPressed()
        }
    }
}

data class FieldHeaderItem(
    val title: String
) : ListItem

data class FieldTextItem(
    val title: String,
    val text: String
) : ListItem

data class FieldUrlItem(
    val title: String,
    val url: String
) : ListItem {

    val isOpenAvailable: Boolean = url.isNotEmpty()
}

data class FieldEmailItem(
    val title: String,
    val email: String
) : ListItem {

    val isOpenAvailable: Boolean = email.isNotEmpty()
}

data class FieldSingleSelectItem(
    val title: String,
    val text: String,
    val values: List<FieldData.EnumItemData>,
    val fieldSchema: FiberyFieldSchema,
    val singleSelectData: FieldData.SingleSelectFieldData
) : ListItem

data class FieldMultiSelectItem(
    val title: String,
    val text: String,
    val values: List<FieldData.EnumItemData>,
    val fieldSchema: FiberyFieldSchema,
    val multiSelectData: FieldData.MultiSelectFieldData
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
    val fieldSchema: FiberyFieldSchema
) : ListItem

data class FieldCheckboxItem(
    val title: String,
    val value: Boolean
) : ListItem
