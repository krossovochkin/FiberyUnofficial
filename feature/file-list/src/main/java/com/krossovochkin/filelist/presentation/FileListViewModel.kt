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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.krossovochkin.core.presentation.color.ColorUtils
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.paging.PaginatedListViewModelDelegate
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.fiberyunofficial.domain.FiberyFileData
import com.krossovochkin.filelist.domain.DownloadFileInteractor
import com.krossovochkin.filelist.domain.GetFileListInteractor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class FileListViewModel : ViewModel() {

    abstract val error: Flow<Exception>

    abstract val navigation: Flow<FileListNavEvent>

    abstract val entityItems: Flow<PagingData<ListItem>>

    abstract val toolbarViewState: ToolbarViewState

    abstract fun downloadFile(fileData: FiberyFileData)

    abstract fun onBackPressed()

    abstract fun onError(error: Exception)
}

internal class FileListViewModelImpl(
    getFileListInteractor: GetFileListInteractor,
    private val downloadFileInteractor: DownloadFileInteractor,
    private val fileListArgs: FileListFragment.Args
) : FileListViewModel() {

    private val paginatedListDelegate = PaginatedListViewModelDelegate(
        viewModel = this,
        loadPage = { offset: Int, pageSize: Int ->
            getFileListInteractor
                .execute(
                    fileListArgs.entityTypeSchema,
                    fileListArgs.parentEntityData,
                    offset,
                    pageSize
                )
        },
        mapper = { file ->
            FileListItem(
                title = file.title,
                fileData = file,
            )
        }
    )

    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    override val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()
    private val navigationChannel = Channel<FileListNavEvent>(Channel.BUFFERED)
    override val navigation: Flow<FileListNavEvent>
        get() = navigationChannel.receiveAsFlow()

    override val entityItems: Flow<PagingData<ListItem>>
        get() = paginatedListDelegate.items

    override val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = fileListArgs.parentEntityData.fieldSchema.displayName,
            bgColorInt = ColorUtils.getColor(fileListArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true
        )

    override fun onBackPressed() {
        viewModelScope.launch {
            navigationChannel.send(FileListNavEvent.BackEvent)
        }
    }

    override fun downloadFile(fileData: FiberyFileData) {
        viewModelScope.launch {
            downloadFileInteractor.execute(fileData)
        }
    }

    override fun onError(error: Exception) {
        viewModelScope.launch {
            this@FileListViewModelImpl.errorChannel.send(error)
        }
    }
}
