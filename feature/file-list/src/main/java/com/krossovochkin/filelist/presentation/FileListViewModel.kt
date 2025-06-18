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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.paging.PaginatedListViewModelDelegate
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.fiberyunofficial.domain.FiberyFileData
import com.krossovochkin.filelist.domain.DownloadFileInteractor
import com.krossovochkin.filelist.domain.GetFileListInteractor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FileListViewModel @AssistedInject constructor(
    getFileListInteractor: GetFileListInteractor,
    private val downloadFileInteractor: DownloadFileInteractor,
    @Assisted private val fileListArgs: FileListFragment.Args
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(args: FileListFragment.Args): FileListViewModel
    }

    companion object {
        fun provideFactory(
            factory: Factory,
            args: FileListFragment.Args
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return factory.create(args) as T
            }
        }
    }

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
    val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()
    private val navigationChannel = Channel<FileListNavEvent>(Channel.BUFFERED)
    val navigation: Flow<FileListNavEvent>
        get() = navigationChannel.receiveAsFlow()

    val entityItems: Flow<PagingData<ListItem>>
        get() = paginatedListDelegate.items

    val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = NativeText.Simple(fileListArgs.parentEntityData.fieldSchema.displayName),
            bgColor = NativeColor.Hex(fileListArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true
        )

    fun onBackPressed() {
        viewModelScope.launch {
            navigationChannel.send(FileListNavEvent.BackEvent)
        }
    }

    fun downloadFile(fileData: FiberyFileData) {
        viewModelScope.launch {
            downloadFileInteractor.execute(fileData)
        }
    }

    fun onError(error: Exception) {
        viewModelScope.launch {
            this@FileListViewModel.errorChannel.send(error)
        }
    }
}
