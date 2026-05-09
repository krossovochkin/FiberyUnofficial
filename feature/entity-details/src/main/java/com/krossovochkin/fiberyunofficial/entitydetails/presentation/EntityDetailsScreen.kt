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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Delete
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarAction
import com.krossovochkin.core.presentation.resources.resolveNativeColor
import com.krossovochkin.core.presentation.resources.resolveNativeText
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityDetailsScreen(
    viewModel: EntityDetailsViewModel,
    onBack: () -> Unit,
    onEntitySelected: (FiberyEntityData) -> Unit,
    onEntityFieldEdit: (ParentEntityData, FiberyEntityData?) -> Unit,
    onEntityTypeSelected: (FiberyEntityTypeSchema, ParentEntityData) -> Unit,
    onSingleSelectFieldEdit:
    (ParentEntityData, com.krossovochkin.fiberyunofficial.domain.FieldData.SingleSelectFieldData) -> Unit,
    onMultiSelectFieldEdit:
    (ParentEntityData, com.krossovochkin.fiberyunofficial.domain.FieldData.MultiSelectFieldData) -> Unit,
) {
    val items by viewModel.items.collectAsState(emptyList())
    val isLoading by viewModel.progress.collectAsState(false)
    val toolbarState = viewModel.toolbarViewState
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.error) {
        viewModel.error.collectLatest { error ->
            snackbarHostState.showSnackbar(message = error.message ?: "Unknown error")
        }
    }

    LaunchedEffect(viewModel.navigation) {
        viewModel.navigation.collectLatest { event ->
            when (event) {
                is EntityDetailsViewModel.EntityDetailsNavigation.Back -> onBack()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    toolbarState.actions.forEach { action ->
                        when (action) {
                            ToolbarAction.DELETE -> {
                                IconButton(onClick = { viewModel.deleteEntity() }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )
                                }
                            }
                            else -> Unit
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(context.resolveNativeColor(toolbarState.bgColor)),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(
                    count = items.size,
                    key = { index -> index }
                ) { index ->
                    val item = items[index]
                    when (item) {
                        is FieldHeaderItem -> Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(16.dp)
                        )
                        is FieldTextItem -> Text(
                            text = "${item.title}: ${item.text}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                        is FieldSingleSelectItem -> Text(
                            text = "${item.title}: ${item.text}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSingleSelectFieldEdit(
                                        ParentEntityData(item.fieldSchema, viewModel.entityData),
                                        item.singleSelectData
                                    )
                                }
                                .padding(16.dp)
                        )
                        is FieldMultiSelectItem -> Text(
                            text = "${item.title}: ${item.text}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onMultiSelectFieldEdit(
                                        ParentEntityData(item.fieldSchema, viewModel.entityData),
                                        item.multiSelectData
                                    )
                                }
                                .padding(16.dp)
                        )
                        is FieldRelationItem -> Text(
                            text = "${item.title}: ${item.entityName}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    item.entityData?.let(onEntitySelected)
                                        ?: onEntityFieldEdit(
                                            ParentEntityData(item.fieldSchema, viewModel.entityData),
                                            null
                                        )
                                }
                                .padding(16.dp)
                        )
                        is FieldCollectionItem -> Text(
                            text = "${item.title}: ${item.countText}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onEntityTypeSelected(
                                        item.entityTypeSchema,
                                        ParentEntityData(item.fieldSchema, viewModel.entityData)
                                    )
                                }
                                .padding(16.dp)
                        )
                        is FieldCheckboxItem -> Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Checkbox(
                                checked = item.value,
                                onCheckedChange = null,
                                enabled = false
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
