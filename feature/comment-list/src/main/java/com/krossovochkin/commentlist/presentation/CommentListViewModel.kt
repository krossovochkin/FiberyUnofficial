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

package com.krossovochkin.commentlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.krossovochkin.commentlist.domain.GetCommentListInteractor
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.paging.PaginatedListViewModelDelegate
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class CommentListViewModel @AssistedInject constructor(
    getCommentListInteractor: GetCommentListInteractor,
    @Assisted private val commentListArgs: CommentListFragment.Args
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(commentListArgs: CommentListFragment.Args): CommentListViewModel
    }

    companion object {
        fun provideFactory(
            factory: Factory,
            commentListArgs: CommentListFragment.Args
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return factory.create(commentListArgs) as T
            }
        }
    }

    private val paginatedListDelegate = PaginatedListViewModelDelegate(
        viewModel = this,
        loadPage = { offset: Int, pageSize: Int ->
            getCommentListInteractor
                .execute(
                    commentListArgs.entityTypeSchema,
                    commentListArgs.parentEntityData,
                    offset,
                    pageSize
                )
        },
        mapper = { comment ->
            CommentListItem(
                authorName = comment.authorName,
                createDate = comment.createDate.format(
                    DateTimeFormatter
                        .ofLocalizedDateTime(FormatStyle.MEDIUM)
                        .withZone(ZoneId.systemDefault())
                ),
                text = comment.text,
                commentData = comment,
            )
        }
    )

    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()
    private val navigationChannel = Channel<CommentListNavEvent>(Channel.BUFFERED)
    val navigation: Flow<CommentListNavEvent>
        get() = navigationChannel.receiveAsFlow()

    val entityItems: Flow<PagingData<ListItem>>
        get() = paginatedListDelegate.items

    val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = NativeText.Simple(commentListArgs.parentEntityData.fieldSchema.displayName),
            bgColor = NativeColor.Hex(commentListArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true
        )

    fun onBackPressed() {
        viewModelScope.launch {
            navigationChannel.send(CommentListNavEvent.BackEvent)
        }
    }

    fun onError(error: Exception) {
        viewModelScope.launch {
            this@CommentListViewModel.errorChannel.send(error)
        }
    }
}
