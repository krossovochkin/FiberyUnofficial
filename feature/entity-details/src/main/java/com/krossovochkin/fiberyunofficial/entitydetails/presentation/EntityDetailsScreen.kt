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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.krossovochkin.core.presentation.resources.resolveNativeText
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import io.noties.markwon.Markwon
import androidx.compose.material3.LocalTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityDetailsScreen(
    viewModel: EntityDetailsViewModel,
    onBackPressed: () -> Unit,
    onDeleteClicked: () -> Unit,
    onFieldHeaderClicked: (FieldHeaderItem) -> Unit,
    onTextFieldClicked: (FieldTextItem) -> Unit,
    onUrlFieldClicked: (FieldUrlItem) -> Unit,
    onEmailFieldClicked: (FieldEmailItem) -> Unit,
    onSingleSelectClicked: (FieldSingleSelectItem) -> Unit,
    onMultiSelectClicked: (FieldMultiSelectItem) -> Unit,
    onRelationFieldClicked: (FiberyFieldSchema, FiberyEntityData?, View) -> Unit,
    onRelationOpenClicked: (FiberyEntityData, View) -> Unit,
    onRelationDeleteClicked: (FiberyFieldSchema) -> Unit,
    onCollectionFieldClicked: (FiberyEntityTypeSchema, FiberyFieldSchema, View) -> Unit,
) {
    val items by viewModel.items.collectAsState(emptyList())
    val isLoading by viewModel.progress.collectAsState(false)
    val toolbarState = viewModel.toolbarViewState
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = context.resolveNativeText(toolbarState.title).toString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                    IconButton(onClick = onDeleteClicked) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(
                        android.graphics.Color.parseColor(
                            toolbarState.bgColor.let {
                                when (it) {
                                    is com.krossovochkin.core.presentation.resources.NativeColor.Hex -> it.colorHex
                                    else -> "#FFFFFF"
                                }
                            }
                        )
                    )
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                itemsIndexed(
                    items = items.filterIsInstance<FieldHeaderItem>() + items.filter { it !is FieldHeaderItem }
                ) { index, item ->
                    when (item) {
                        is FieldHeaderItem -> {
                            FieldHeaderRow(
                                item = item,
                                onClick = { onFieldHeaderClicked(item) }
                            )
                        }
                        is FieldTextItem -> {
                            FieldTextRow(
                                item = item,
                                onClick = { onTextFieldClicked(item) }
                            )
                        }
                        is FieldUrlItem -> {
                            FieldUrlRow(
                                item = item,
                                onClick = { onUrlFieldClicked(item) }
                            )
                        }
                        is FieldEmailItem -> {
                            FieldEmailRow(
                                item = item,
                                onClick = { onEmailFieldClicked(item) }
                            )
                        }
                        is FieldSingleSelectItem -> {
                            FieldSingleSelectRow(
                                item = item,
                                onClick = { onSingleSelectClicked(item) }
                            )
                        }
                        is FieldMultiSelectItem -> {
                            FieldMultiSelectRow(
                                item = item,
                                onClick = { onMultiSelectClicked(item) }
                            )
                        }
                        is FieldRichTextItem -> {
                            FieldRichTextRow(item = item)
                        }
                        is FieldRelationItem -> {
                            FieldRelationRow(
                                item = item,
                                onFieldClicked = { onRelationFieldClicked(item.fieldSchema, item.entityData, View(android.view.ContextThemeWrapper())) },
                                onOpenClicked = { item.entityData?.let { onRelationOpenClicked(it, View(android.view.ContextThemeWrapper())) } },
                                onDeleteClicked = { onRelationDeleteClicked(item.fieldSchema) }
                            )
                        }
                        is FieldCollectionItem -> {
                            FieldCollectionRow(
                                item = item,
                                onClick = { onCollectionFieldClicked(item.entityTypeSchema, item.fieldSchema, View(android.view.ContextThemeWrapper())) }
                            )
                        }
                        is FieldCheckboxItem -> {
                            FieldCheckboxRow(item = item)
                        }
                    }

                    if (index < items.size - 1) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
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

@Composable
fun FieldHeaderRow(
    item: FieldHeaderItem,
    onClick: () -> Unit,
) {
    Text(
        text = item.title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun FieldTextRow(
    item: FieldTextItem,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = item.text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun FieldUrlRow(
    item: FieldUrlItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = item.isOpenAvailable) { onClick() }
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = item.url,
            style = MaterialTheme.typography.bodyLarge,
            color = if (item.isOpenAvailable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun FieldEmailRow(
    item: FieldEmailItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = item.isOpenAvailable) { onClick() }
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = item.email,
            style = MaterialTheme.typography.bodyLarge,
            color = if (item.isOpenAvailable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun FieldSingleSelectRow(
    item: FieldSingleSelectItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = item.text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun FieldMultiSelectRow(
    item: FieldMultiSelectItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = item.text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun FieldRichTextRow(
    item: FieldRichTextItem,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.material3.Text(
            text = item.value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun FieldRelationRow(
    item: FieldRelationItem,
    onFieldClicked: () -> Unit,
    onOpenClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFieldClicked() }
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.entityName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (item.isOpenAvailable) {
                    IconButton(onClick = onOpenClicked, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Open",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                if (item.isDeleteAvailable) {
                    IconButton(onClick = onDeleteClicked, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FieldCollectionRow(
    item: FieldCollectionItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = item.countText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun FieldCheckboxRow(
    item: FieldCheckboxItem,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        androidx.compose.material3.Checkbox(
            checked = item.value,
            onCheckedChange = null,
            enabled = false
        )
    }
}
