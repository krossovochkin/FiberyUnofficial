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
package com.krossovochkin.fiberyunofficial.pickersort.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
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
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.pickersort.R
import com.krossovochkin.fiberyunofficial.pickersort.domain.SortCondition

private val ContentMaxWidth = 640.dp
private val WideLayoutThreshold = 600.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerSortScreen(
    viewModel: PickerSortViewModel,
    onBack: () -> Unit,
    onSortApply: (FiberyEntityTypeSchema, FiberyEntitySortData) -> Unit,
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
            val hasEmptySlot = uiState.rows.any { it is EmptySortItem }
            val hasSorts = uiState.rows.any { it is SelectedSortItem || it is EmptySortItem }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (hasSorts) {
                    item(key = "hint", contentType = "hint") {
                        Text(
                            text = stringResource(id = R.string.picker_sort_hint_order),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = ContentMaxWidth)
                        )
                    }
                }

                itemsIndexed(
                    items = uiState.rows,
                    key = { _, item ->
                        when (item) {
                            is SelectedSortItem -> "selected-${item.key}"
                            is EmptySortItem -> "empty-${item.key}"
                            else -> "add"
                        }
                    },
                    contentType = { _, item ->
                        when (item) {
                            is SelectedSortItem -> "selected"
                            is EmptySortItem -> "empty"
                            else -> "add"
                        }
                    }
                ) { index, item ->
                    when (item) {
                        is SelectedSortItem -> {
                            SelectedSortCard(
                                item = item,
                                onFieldSelected = { viewModel.onFieldSelected(index, it) },
                                onConditionSelected = { viewModel.onConditionSelected(index, it) },
                                onDeleteClicked = { viewModel.onDeleteClicked(index) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = ContentMaxWidth)
                            )
                        }

                        is EmptySortItem -> {
                            EmptySortCard(
                                item = item,
                                onFieldSelected = { viewModel.onFieldSelected(index, it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = ContentMaxWidth)
                            )
                        }

                        is AddSortItem -> {
                            if (!hasEmptySlot) {
                                AddSortButton(
                                    onClick = { viewModel.onAddSortClicked() },
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
                        uiState.isApplyEnabled -> null
                        uiState.validSortCount == 0 -> stringResource(id = R.string.picker_sort_apply_hint_empty)
                        uiState.hasIncompleteRows -> stringResource(id = R.string.picker_sort_apply_hint_incomplete)
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
                        onClick = { viewModel.applySort(onSortApply) }
                    ) {
                        val count = uiState.validSortCount
                        Text(
                            text = if (count > 0) {
                                stringResource(id = R.string.picker_sort_apply_action_count, count)
                            } else {
                                stringResource(id = R.string.picker_sort_apply_action)
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun indexOf(item: com.krossovochkin.fiberyunofficial.ui.list.ListItem, uiState: PickerSortUiState): Int {
    return uiState.rows.indexOf(item)
}

@Composable
private fun SelectedSortCard(
    item: SelectedSortItem,
    onFieldSelected: (FiberyFieldSchema?) -> Unit,
    onConditionSelected: (SortCondition?) -> Unit,
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
                        text = stringResource(id = R.string.picker_sort_sort_title, item.order),
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
                        contentDescription = stringResource(id = R.string.picker_sort_delete_content_description),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                if (maxWidth >= WideLayoutThreshold) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FieldDropdown(
                            fields = item.fields,
                            selectedField = item.field,
                            isError = item.isDuplicateField,
                            errorText = if (item.isDuplicateField) {
                                stringResource(id = R.string.picker_sort_field_duplicate)
                            } else {
                                null
                            },
                            onFieldSelected = onFieldSelected,
                            modifier = Modifier.weight(1f)
                        )
                        DirectionDropdown(
                            conditions = item.conditions,
                            selectedCondition = item.condition,
                            isError = item.showConditionError,
                            onConditionSelected = onConditionSelected,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FieldDropdown(
                            fields = item.fields,
                            selectedField = item.field,
                            isError = item.isDuplicateField,
                            errorText = if (item.isDuplicateField) {
                                stringResource(id = R.string.picker_sort_field_duplicate)
                            } else {
                                null
                            },
                            onFieldSelected = onFieldSelected,
                            modifier = Modifier.fillMaxWidth()
                        )
                        DirectionDropdown(
                            conditions = item.conditions,
                            selectedCondition = item.condition,
                            isError = item.showConditionError,
                            onConditionSelected = onConditionSelected,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySortCard(
    item: EmptySortItem,
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
                text = stringResource(id = R.string.picker_sort_empty_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = R.string.picker_sort_empty_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            FieldDropdown(
                fields = item.fields,
                selectedField = null,
                isError = false,
                errorText = null,
                onFieldSelected = onFieldSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AddSortButton(
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
        Text(text = stringResource(id = R.string.picker_sort_add_action))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldDropdown(
    fields: List<FiberyFieldSchema>,
    selectedField: FiberyFieldSchema?,
    isError: Boolean,
    errorText: String?,
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
            label = { Text(text = stringResource(id = R.string.picker_sort_field_label)) },
            placeholder = { Text(text = stringResource(id = R.string.picker_sort_field_placeholder)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = null
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            supportingText = errorText?.let { { Text(text = it) } },
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
private fun DirectionDropdown(
    conditions: List<SortCondition>,
    selectedCondition: SortCondition?,
    isError: Boolean,
    onConditionSelected: (SortCondition?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCondition?.let { stringResource(id = it.displayNameResId) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(id = R.string.picker_sort_direction_label)) },
            placeholder = { Text(text = stringResource(id = R.string.picker_sort_direction_placeholder)) },
            leadingIcon = {
                when (selectedCondition) {
                    SortCondition.ASCENDING -> Icon(
                        imageVector = Icons.Filled.ArrowUpward,
                        contentDescription = null
                    )

                    SortCondition.DESCENDING -> Icon(
                        imageVector = Icons.Filled.ArrowDownward,
                        contentDescription = null
                    )

                    null -> Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = null
                    )
                }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            supportingText = if (isError) {
                { Text(text = stringResource(id = R.string.picker_sort_direction_required)) }
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
                    text = { Text(text = stringResource(id = condition.displayNameResId)) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (condition) {
                                SortCondition.ASCENDING -> Icons.Filled.ArrowUpward
                                SortCondition.DESCENDING -> Icons.Filled.ArrowDownward
                            },
                            contentDescription = null
                        )
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
                    },
                    onClick = {
                        onConditionSelected(condition)
                        expanded = false
                    }
                )
            }
        }
    }
}
