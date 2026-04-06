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
package com.krossovochkin.filelist.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.filelist.R
import kotlinx.coroutines.flow.Flow

@Composable
fun FileListScreen(
    itemsFlow: Flow<PagingData<ListItem>>,
    onDownloadClick: (FileListItem) -> Unit,
    onError: (Exception) -> Unit,
) {
    val lazyItems = itemsFlow.collectAsLazyPagingItems()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                count = lazyItems.itemCount,
                key = { index ->
                    val item = lazyItems[index]
                    if (item is FileListItem) item.fileData.id else index
                }
            ) { index ->
                val item = lazyItems[index]
                if (item is FileListItem) {
                    FileListItemRow(
                        item = item,
                        onDownloadClick = { onDownloadClick(item) }
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

        if (lazyItems.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun FileListItemRow(
    item: FileListItem,
    onDownloadClick: () -> Unit,
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
        IconButton(onClick = onDownloadClick) {
            Icon(
                painter = painterResource(id = R.drawable.file_list_ic_baseline_cloud_download_24),
                contentDescription = stringResource(
                    id = R.string.file_list_content_description_download
                )
            )
        }
    }
}
