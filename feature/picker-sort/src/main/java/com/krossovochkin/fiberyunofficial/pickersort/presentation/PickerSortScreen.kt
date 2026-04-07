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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krossovochkin.core.presentation.resources.resolveNativeColor
import com.krossovochkin.core.presentation.resources.resolveNativeText
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.pickersort.R
import com.krossovochkin.fiberyunofficial.pickersort.domain.SortCondition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerSortScreen(
    viewModel: PickerSortViewModel,
    onBackPressed: () -> Unit,
    onSortApply: () -> Unit,
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
                    IconButton(onClick = onBackPressed) {
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
                        is SelectedSortItem -> {
                            SortItemRow(
                                item = item,
                                onFieldSelected = { viewModel.onFieldSelected(index, it) },
                                onConditionSelected = { viewModel.onConditionSelected(index, it) }
                            )
                        }

                        is EmptySortItem -> {
                            EmptySortItemRow(
                                item = item,
                                onFieldSelected = { viewModel.onFieldSelected(index, it) }
                            )
                        }

                        is AddSortItem -> {
                            AddSortItemRow(
                                onClick = { viewModel.onAddSortClicked() }
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
                onClick = onSortApply
            ) {
                Text(text = stringResource(id = R.string.picker_sort_apply_action))
            }
        }
    }
}

@Composable
fun SortItemRow(
    item: SelectedSortItem,
    onFieldSelected: (FiberyFieldSchema?) -> Unit,
    onConditionSelected: (SortCondition?) -> Unit,
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
            itemLabel = { it?.let { stringResource(id = it.displayNameResId) } ?: "" }
        )
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
}

@Composable
fun EmptySortItemRow(
    item: EmptySortItem,
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
fun AddSortItemRow(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = "Add Sort",
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
