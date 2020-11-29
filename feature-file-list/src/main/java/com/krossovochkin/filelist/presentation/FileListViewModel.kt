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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFileData
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.ToolbarViewState
import by.krossovochkin.fiberyunofficial.core.presentation.common.PaginatedListViewModelDelegate
import com.krossovochkin.filelist.domain.DownloadFileInteractor
import com.krossovochkin.filelist.domain.GetFileListInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FileListViewModel(
    getFileListInteractor: GetFileListInteractor,
    private val downloadFileInteractor: DownloadFileInteractor,
    private val fileListArgs: FileListFragment.Args
) : ViewModel() {

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

    private val mutableError = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = mutableError

    private val mutableNavigation = MutableLiveData<Event<FileListNavEvent>>()
    val navigation: LiveData<Event<FileListNavEvent>> = mutableNavigation

    val entityItems: Flow<PagingData<ListItem>>
        get() = paginatedListDelegate.items

    val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = fileListArgs.parentEntityData.fieldSchema.displayName,
            bgColorInt = ColorUtils.getColor(fileListArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true
        )

    fun onBackPressed() {
        mutableNavigation.value = Event(FileListNavEvent.BackEvent)
    }

    fun downloadFile(fileData: FiberyFileData) {
        viewModelScope.launch {
            downloadFileInteractor.execute(fileData)
        }
    }

    fun onError(error: Exception) {
        mutableError.postValue(Event(error))
    }
}
