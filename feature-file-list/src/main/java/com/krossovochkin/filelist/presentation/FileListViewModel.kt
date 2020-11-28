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
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.map
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFileData
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.ToolbarViewState
import com.krossovochkin.filelist.domain.DownloadFileInteractor
import com.krossovochkin.filelist.domain.GetFileListInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

class FileListViewModel(
    getFileListInteractor: GetFileListInteractor,
    private val downloadFileInteractor: DownloadFileInteractor,
    private val fileListArgs: FileListFragment.Args
) : ViewModel() {

    private val mutableError = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = mutableError

    private val mutableNavigation = MutableLiveData<Event<FileListNavEvent>>()
    val navigation: LiveData<Event<FileListNavEvent>> = mutableNavigation

    private var dataSource: EntityListDataSource? = null
    private val pager = Pager(
        PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false
        )
    ) {
        EntityListDataSource(fileListArgs, getFileListInteractor)
            .also { dataSource = it }
    }

    val entityItems: Flow<PagingData<ListItem>>
        get() = pager
            .flow
            .map {
                it.map<FiberyFileData, ListItem> { file ->
                    FileListItem(
                        title = file.title,
                        fileData = file,
                    )
                }
            }
            .cachedIn(viewModelScope)

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

private class EntityListDataSource(
    private val entityListArgs: FileListFragment.Args,
    private val getFileListInteractor: GetFileListInteractor
) : PagingSource<Int, FiberyFileData>() {

    private suspend fun loadPage(offset: Int, pageSize: Int): LoadResult<Int, FiberyFileData> {
        return try {
            val pageData = getFileListInteractor
                .execute(
                    entityListArgs.entityTypeSchema,
                    entityListArgs.parentEntityData,
                    offset,
                    pageSize
                )
            LoadResult.Page(
                data = pageData,
                prevKey = null,
                nextKey = if (pageData.size == pageSize) offset + pageSize else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, FiberyFileData> {
        return when (params) {
            is LoadParams.Refresh -> loadPage(0, params.loadSize)
            is LoadParams.Append -> loadPage(params.key, params.loadSize)
            is LoadParams.Prepend -> LoadResult.Error(UnsupportedOperationException())
        }
    }
}
