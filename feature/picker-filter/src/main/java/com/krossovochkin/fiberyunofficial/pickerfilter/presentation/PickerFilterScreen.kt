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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.krossovochkin.core.presentation.resources.resolveNativeText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerFilterScreen(
    viewModel: PickerFilterViewModel,
    onBackPressed: () -> Unit,
    onFilterApply: () -> Unit,
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(items = items) { item ->
                when (item) {
                    is FilterMergeTypeItem -> {
                        FilterMergeTypeItemRow(item = item)
                    }
                    is EmptyFilterItem -> {
                        EmptyFilterItemRow()
                    }
                    is SingleSelectFilterItem -> {
                        SingleSelectFilterItemRow(item = item)
                    }
                    is AddFilterItem -> {
                        AddFilterItemRow()
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun FilterMergeTypeItemRow(item: FilterMergeTypeItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White)
    ) {
        Text(
            text = item.type.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Divider(modifier = Modifier.fillMaxWidth())
}

@Composable
fun EmptyFilterItemRow() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White)
    ) {
        Text(
            text = "Add Filter",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun SingleSelectFilterItemRow(item: SingleSelectFilterItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White)
    ) {
        Text(
            text = item.field.name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun AddFilterItemRow() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White)
    ) {
        Text(
            text = "Add Filter",
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Divider(modifier = Modifier.fillMaxWidth())
}
