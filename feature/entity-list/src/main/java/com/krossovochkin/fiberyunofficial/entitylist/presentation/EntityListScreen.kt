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
package com.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.krossovochkin.core.presentation.resources.resolveNativeColor
import com.krossovochkin.core.presentation.resources.resolveNativeText
import com.krossovochkin.fiberyunofficial.entitylist.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityListScreen(
    viewModel: EntityListViewModel,
    onBackPressed: () -> Unit,
    onEntitySelected: (EntityListItem) -> Unit,
    onRemoveRelation: (EntityListItem) -> Unit,
    onCreateEntityClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onSortClicked: () -> Unit,
    onError: (Exception) -> Unit,
) {
    val lazyItems = viewModel.entityItems.collectAsLazyPagingItems()
    val toolbarViewState = viewModel.toolbarViewState
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = context.resolveNativeText(toolbarViewState.title).toString()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (toolbarViewState.menuResId == R.menu.entity_list_menu) {
                        IconButton(onClick = { onFilterClicked() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.entity_list_ic_filter_list_white_24dp),
                                contentDescription = stringResource(id = R.string.entity_list_action_filter)
                            )
                        }
                        IconButton(onClick = { onSortClicked() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.entity_list_ic_sort_white_24dp),
                                contentDescription = stringResource(id = R.string.entity_list_action_sort)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(context.resolveNativeColor(toolbarViewState.bgColor)),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onCreateEntityClicked() },
                containerColor = Color(context.resolveNativeColor(viewModel.getCreateFabViewState().bgColor)),
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.entity_list_entityList_create)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    count = lazyItems.itemCount,
                    key = { index ->
                        val item = lazyItems[index]
                        if (item is EntityListItem) item.entityData.id else index
                    }
                ) { index ->
                    val item = lazyItems[index]
                    if (item is EntityListItem) {
                        EntityListItemRow(
                            item = item,
                            onClick = { onEntitySelected(item) },
                            onRemoveRelation = { onRemoveRelation(item) }
                        )
                        HorizontalDivider()
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

            if (lazyItems.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun EntityListItemRow(
    item: EntityListItem,
    onClick: () -> Unit,
    onRemoveRelation: () -> Unit,
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            if (item.isRemoveAvailable) {
                IconButton(onClick = onRemoveRelation) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(
                            id = R.string.entity_list_content_description_remove_relation
                        )
                    )
                }
            }
        }
    }
}
