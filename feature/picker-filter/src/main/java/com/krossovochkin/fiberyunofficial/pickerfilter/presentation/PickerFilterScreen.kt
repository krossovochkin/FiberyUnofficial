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
package com.krossovochkin.fiberyunofficial.pickerfilter.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.krossovochkin.core.presentation.resources.resolveNativeColor
import com.krossovochkin.core.presentation.resources.resolveNativeText
import com.krossovochkin.core.presentation.ui.toolbar.toolbarContentColor
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.pickerfilter.R
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterCondition
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterMergeType

private val ContentMaxWidth = 640.dp
private val WideLayoutThreshold = 600.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerFilterScreen(
    viewModel: PickerFilterViewModel,
    onBack: () -> Unit,
    onFilterApply: (FiberyEntityTypeSchema, FiberyEntityFilterData) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val toolbarState = viewModel.toolbarViewState
    val context = LocalContext.current

    Scaffold(
        topBar = {
            val toolbarContainerColor =
                Color(context.resolveNativeColor(toolbarState.bgColor))
            val toolbarContentColor = toolbarContainerColor.toolbarContentColor()
            TopAppBar(
                title = {
                    Text(
                        text = context.resolveNativeText(toolbarState.title).toString()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = toolbarContainerColor,
                    titleContentColor = toolbarContentColor,
                    navigationIconContentColor = toolbarContentColor,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val hasEmptySlot = uiState.rows.any { it is EmptyFilterItem }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(
                    items = uiState.rows,
                    key = { _, item ->
                        when (item) {
                            is FilterMergeTypeItem -> "merge"
                            is SingleSelectFilterItem -> "filter-${item.key}"
                            is EmptyFilterItem -> "empty-${item.key}"
                            else -> "add"
                        }
                    },
                    contentType = { _, item ->
                        when (item) {
                            is FilterMergeTypeItem -> "merge"
                            is SingleSelectFilterItem -> "filter"
                            is EmptyFilterItem -> "empty"
                            else -> "add"
                        }
                    }
                ) { index, item ->
                    // index 0 is the merge-type header, so data rows sit at index - 1
                    val dataPosition = index - 1
                    when (item) {
                        is FilterMergeTypeItem -> {
                            MergeTypeCard(
                                item = item,
                                onMergeTypeSelected = { viewModel.onMergeTypeSelected(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = ContentMaxWidth)
                            )
                        }

                        is SingleSelectFilterItem -> {
                            FilterCard(
                                item = item,
                                onFieldSelected = { viewModel.onFieldSelected(dataPosition, it) },
                                onConditionSelected = { viewModel.onConditionSelected(dataPosition, it) },
                                onValueSelected = { viewModel.onSingleSelectValueSelected(dataPosition, it) },
                                onDeleteClicked = { viewModel.onDeleteClicked(dataPosition) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = ContentMaxWidth)
                            )
                        }

                        is EmptyFilterItem -> {
                            EmptyFilterCard(
                                item = item,
                                onFieldSelected = { viewModel.onFieldSelected(dataPosition, it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = ContentMaxWidth)
                            )
                        }

                        is AddFilterItem -> {
                            if (!hasEmptySlot) {
                                AddFilterButton(
                                    onClick = { viewModel.onAddFilterClicked() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .widthIn(max = ContentMaxWidth)
                                )
                            }
                        }

                        else -> {}
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = ContentMaxWidth)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val hintText = when {
                        !uiState.isApplyEnabled -> stringResource(id = R.string.picker_filter_apply_hint_incomplete)
                        uiState.validFilterCount == 0 -> stringResource(id = R.string.picker_filter_apply_hint_clear)
                        else -> null
                    }
                    if (hintText != null) {
                        Text(
                            text = hintText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.isApplyEnabled,
                        onClick = { viewModel.applyFilter(onFilterApply) }
                    ) {
                        val count = uiState.validFilterCount
                        Text(
                            text = if (count > 0) {
                                stringResource(id = R.string.picker_filter_apply_action_count, count)
                            } else {
                                stringResource(id = R.string.picker_filter_apply_action)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MergeTypeCard(
    item: FilterMergeTypeItem,
    onMergeTypeSelected: (FilterMergeType) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(
                    id = R.string.picker_filter_match_title,
                    stringResource(id = item.type.displayNameResId)
                ),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterMergeType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = type == item.type,
                        onClick = { onMergeTypeSelected(type) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = FilterMergeType.entries.size
                        ),
                        label = { Text(text = stringResource(id = type.displayNameResId)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterCard(
    item: SingleSelectFilterItem,
    onFieldSelected: (FiberyFieldSchema?) -> Unit,
    onConditionSelected: (FilterCondition?) -> Unit,
    onValueSelected: (FieldData.EnumItemData?) -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Text(
                        text = item.order.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.picker_filter_title, item.order),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.field.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDeleteClicked) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(id = R.string.picker_filter_delete_content_description),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                if (maxWidth >= WideLayoutThreshold) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FieldDropdown(
                            fields = item.fields,
                            selectedField = item.field,
                            onFieldSelected = onFieldSelected,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ConditionDropdown(
                                conditions = item.conditions,
                                selectedCondition = item.condition,
                                isError = item.showConditionError,
                                onConditionSelected = onConditionSelected,
                                modifier = Modifier.weight(1f)
                            )
                            ValueDropdown(
                                values = item.values,
                                selectedValue = item.selectedValue,
                                isError = item.showValueError,
                                onValueSelected = onValueSelected,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FieldDropdown(
                            fields = item.fields,
                            selectedField = item.field,
                            onFieldSelected = onFieldSelected,
                            modifier = Modifier.fillMaxWidth()
                        )
                        ConditionDropdown(
                            conditions = item.conditions,
                            selectedCondition = item.condition,
                            isError = item.showConditionError,
                            onConditionSelected = onConditionSelected,
                            modifier = Modifier.fillMaxWidth()
                        )
                        ValueDropdown(
                            values = item.values,
                            selectedValue = item.selectedValue,
                            isError = item.showValueError,
                            onValueSelected = onValueSelected,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFilterCard(
    item: EmptyFilterItem,
    onFieldSelected: (FiberyFieldSchema?) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(id = R.string.picker_filter_empty_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = R.string.picker_filter_empty_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            FieldDropdown(
                fields = item.fields,
                selectedField = null,
                onFieldSelected = onFieldSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AddFilterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = stringResource(id = R.string.picker_filter_add_action))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldDropdown(
    fields: List<FiberyFieldSchema>,
    selectedField: FiberyFieldSchema?,
    onFieldSelected: (FiberyFieldSchema?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedField?.displayName ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = fields.isNotEmpty(),
            label = { Text(text = stringResource(id = R.string.picker_filter_field_label)) },
            placeholder = { Text(text = stringResource(id = R.string.picker_filter_field_placeholder)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = null
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            fields.forEach { field ->
                val selected = field.name == selectedField?.name
                DropdownMenuItem(
                    text = { Text(text = field.displayName) },
                    onClick = {
                        onFieldSelected(field)
                        expanded = false
                    },
                    trailingIcon = if (selected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConditionDropdown(
    conditions: List<FilterCondition>,
    selectedCondition: FilterCondition?,
    isError: Boolean,
    onConditionSelected: (FilterCondition?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCondition?.let { stringResource(id = it.displayStringResId) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(id = R.string.picker_filter_condition_label)) },
            placeholder = { Text(text = stringResource(id = R.string.picker_filter_condition_placeholder)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                    contentDescription = null
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            supportingText = if (isError) {
                { Text(text = stringResource(id = R.string.picker_filter_condition_required)) }
            } else {
                null
            },
            singleLine = true,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            conditions.forEach { condition ->
                val selected = condition == selectedCondition
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = condition.displayStringResId)) },
                    onClick = {
                        onConditionSelected(condition)
                        expanded = false
                    },
                    trailingIcon = if (selected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ValueDropdown(
    values: List<FieldData.EnumItemData>,
    selectedValue: FieldData.EnumItemData?,
    isError: Boolean,
    onValueSelected: (FieldData.EnumItemData?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue?.title ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(id = R.string.picker_filter_value_label)) },
            placeholder = { Text(text = stringResource(id = R.string.picker_filter_value_placeholder)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Tag,
                    contentDescription = null
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            supportingText = if (isError) {
                { Text(text = stringResource(id = R.string.picker_filter_value_required)) }
            } else {
                null
            },
            singleLine = true,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            values.forEach { value ->
                val selected = value.id == selectedValue?.id
                DropdownMenuItem(
                    text = { Text(text = value.title) },
                    onClick = {
                        onValueSelected(value)
                        expanded = false
                    },
                    trailingIcon = if (selected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
    }
}
