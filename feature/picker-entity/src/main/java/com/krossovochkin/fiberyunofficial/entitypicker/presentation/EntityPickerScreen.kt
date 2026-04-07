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
package com.krossovochkin.fiberyunofficial.entitypicker.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.resources.resolveNativeColor
import com.krossovochkin.core.presentation.resources.resolveNativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.fiberyunofficial.entitypicker.R
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityPickerScreen(
    itemsFlow: Flow<PagingData<ListItem>>,
    toolbarViewState: Flow<ToolbarViewState>,
    entityCreateEnabled: Flow<Boolean>,
    onItemClick: (EntityPickerItem) -> Unit,
    onCreateClick: () -> Unit,
    onBackPressed: () -> Unit,
    onError: (Exception) -> Unit,
) {
    val lazyItems = itemsFlow.collectAsLazyPagingItems()
    val isCreateEnabled by entityCreateEnabled.collectAsState(false)
    val toolbarState by toolbarViewState.collectAsState(null)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            toolbarState?.let { state ->
                TopAppBar(
                    title = {
                        Text(
                            text = context.resolveNativeText(state.title).toString()
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
                        containerColor = androidx.compose.ui.graphics.Color(context.resolveNativeColor(state.bgColor)),
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                )
            }
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
                    .fillMaxWidth()
            ) {
                items(
                    count = lazyItems.itemCount,
                    key = { index ->
                        val item = lazyItems[index]
                        if (item is EntityPickerItem) item.entityData.id else index
                    }
                ) { index ->
                    val item = lazyItems[index]
                    if (item is EntityPickerItem) {
                        EntityPickerItemRow(
                            item = item,
                            onClick = { onItemClick(item) }
                        )
                    }
                }

                lazyItems.apply {
                    when (loadState.append) {
                        is LoadState.Error -> {
                            val error = (loadState.append as LoadState.Error).error
                            onError(Exception(error.message, error))
                        }
                        is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        is LoadState.NotLoading -> {}
                    }

                    when (loadState.refresh) {
                        is LoadState.Error -> {
                            val error = (loadState.refresh as LoadState.Error).error
                            onError(Exception(error.message, error))
                        }
                        else -> {}
                    }
                }
            }

            Button(
                onClick = onCreateClick,
                enabled = isCreateEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(id = R.string.picker_entity_action_create))
            }

            if (lazyItems.loadState.refresh is LoadState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun EntityPickerItemRow(
    item: EntityPickerItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        HorizontalDivider()
    }
}
