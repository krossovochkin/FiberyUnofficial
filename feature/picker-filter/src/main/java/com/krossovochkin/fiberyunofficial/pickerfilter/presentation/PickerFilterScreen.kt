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
package com.krossovochkin.fiberyunofficial.pickerfilter.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.krossovochkin.core.presentation.resources.resolveNativeColor
import com.krossovochkin.core.presentation.resources.resolveNativeText
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.pickerfilter.R
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterCondition
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterMergeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerFilterScreen(
    viewModel: PickerFilterViewModel,
    onBack: () -> Unit,
    onFilterApply: (FiberyEntityTypeSchema, FiberyEntityFilterData) -> Unit,
) {
    val items by viewModel.items.collectAsState(emptyList())
    val toolbarState = viewModel.toolbarViewState
    val context = LocalContext.current

    Scaffold(
        topBar = {
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
                    containerColor = Color(context.resolveNativeColor(toolbarState.bgColor)),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                itemsIndexed(items = items) { index, item ->
                    when (item) {
                        is FilterMergeTypeItem -> {
                            FilterMergeTypeItemRow(
                                item = item,
                                onMergeTypeSelected = { viewModel.onMergeTypeSelected(it) }
                            )
                        }

                        is EmptyFilterItem -> {
                            EmptyFilterItemRow(
                                item = item,
                                onFieldSelected = { viewModel.onFieldSelected(index - 1, it) }
                            )
                        }

                        is SingleSelectFilterItem -> {
                            SingleSelectFilterItemRow(
                                item = item,
                                onFieldSelected = { viewModel.onFieldSelected(index - 1, it) },
                                onConditionSelected = { viewModel.onConditionSelected(index - 1, it) },
                                onValueSelected = { viewModel.onSingleSelectValueSelected(index - 1, it) }
                            )
                        }

                        is AddFilterItem -> {
                            AddFilterItemRow(
                                onClick = { viewModel.onAddFilterClicked() }
                            )
                        }

                        else -> {}
                    }
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = { viewModel.applyFilter(onFilterApply) }
            ) {
                Text(text = stringResource(id = R.string.picker_filter_apply_action))
            }
        }
    }
}

@Composable
fun FilterMergeTypeItemRow(
    item: FilterMergeTypeItem,
    onMergeTypeSelected: (FilterMergeType) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        FilterMergeType.entries.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onMergeTypeSelected(type) }
                    .padding(end = 16.dp)
            ) {
                RadioButton(
                    selected = type == item.type,
                    onClick = { onMergeTypeSelected(type) }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(id = type.displayNameResId),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
}

@Composable
fun EmptyFilterItemRow(
    item: EmptyFilterItem,
    onFieldSelected: (FiberyFieldSchema?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spinner(
            items = listOf(null) + item.fields,
            selectedItem = null,
            onItemSelected = onFieldSelected,
            itemLabel = { it?.name ?: "" }
        )
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
}

@Composable
fun SingleSelectFilterItemRow(
    item: SingleSelectFilterItem,
    onFieldSelected: (FiberyFieldSchema?) -> Unit,
    onConditionSelected: (FilterCondition?) -> Unit,
    onValueSelected: (FieldData.EnumItemData?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spinner(
            items = listOf(null) + item.fields,
            selectedItem = item.field,
            onItemSelected = onFieldSelected,
            itemLabel = { it?.name ?: "" }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Spinner(
            items = item.conditions,
            selectedItem = item.condition,
            onItemSelected = onConditionSelected,
            itemLabel = { it?.let { stringResource(id = it.displayStringResId) } ?: "" }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Spinner(
            items = item.values,
            selectedItem = item.selectedValue,
            onItemSelected = onValueSelected,
            itemLabel = { it?.title ?: "" }
        )
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
}

@Composable
fun AddFilterItemRow(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = "Add Filter",
            style = MaterialTheme.typography.bodyLarge
        )
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
}

@Composable
fun <T> Spinner(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemLabel: @Composable (T?) -> String,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(vertical = 8.dp),
            text = itemLabel(selectedItem),
            style = MaterialTheme.typography.bodyLarge
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = itemLabel(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
