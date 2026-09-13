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
package com.krossovochkin.fiberyunofficial.entitypicker.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.krossovochkin.core.presentation.resources.resolveNativeColor
import com.krossovochkin.core.presentation.resources.resolveNativeText
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import com.krossovochkin.fiberyunofficial.entitypicker.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityPickerScreen(
    viewModel: EntityPickerViewModel,
    onBack: () -> Unit,
    onEntityPicked: (ParentEntityData, FiberyEntityData?) -> Unit,
) {
    val lazyItems = viewModel.entityItems.collectAsLazyPagingItems()
    val isCreateEnabled by viewModel.entityCreateEnabled.collectAsState(false)
    val searchQuery by viewModel.searchQuery.collectAsState()
    val toolbarState by viewModel.toolbarViewState.collectAsState(null)
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchExpanded) {
        if (isSearchExpanded) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(viewModel.error) {
        viewModel.error.collectLatest { error ->
            snackbarHostState.showSnackbar(message = error.message ?: "Unknown error")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            toolbarState?.let { state ->
                TopAppBar(
                    title = {
                        if (isSearchExpanded) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.onSearchQueryChanged(it) },
                                placeholder = {
                                    Text(
                                        text = stringResource(id = R.string.picker_entity_search_hint),
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                singleLine = true,
                                maxLines = 1,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions.Default,
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear search"
                                            )
                                        }
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                                    focusedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
                                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
                                    focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                )
                            )
                        } else {
                            Text(
                                text = context.resolveNativeText(state.title).toString()
                            )
                        }
                    },
                    navigationIcon = {
                        if (isSearchExpanded) {
                            IconButton(onClick = {
                                viewModel.onSearchQueryChanged("")
                                isSearchExpanded = false
                            }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close search"
                                )
                            }
                        } else {
                            IconButton(onClick = onBack) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                    actions = {
                        if (!isSearchExpanded) {
                            IconButton(onClick = { isSearchExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(id = R.string.picker_entity_search_hint)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.ui.graphics.Color(context.resolveNativeColor(state.bgColor)),
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
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
                                onClick = { onEntityPicked(viewModel.getParentEntityData(), item.entityData) }
                            )
                        }
                    }

                    lazyItems.apply {
                        when (loadState.append) {
                            is LoadState.Error -> {
                                val error = (loadState.append as LoadState.Error).error
                                viewModel.onError(Exception(error.message, error))
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
                                viewModel.onError(Exception(error.message, error))
                            }
                            else -> {}
                        }
                    }
                }

                Button(
                    onClick = { viewModel.createEntity(onEntityPicked) },
                    enabled = isCreateEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(stringResource(id = R.string.picker_entity_action_create))
                }
            }

            if (lazyItems.loadState.refresh is LoadState.Loading && lazyItems.itemCount == 0) {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
