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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.ToolbarViewState
import by.krossovochkin.fiberyunofficial.core.presentation.common.PaginatedListViewModelDelegate
import com.krossovochkin.commentlist.domain.GetCommentListInteractor
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class CommentListViewModel(
    getCommentListInteractor: GetCommentListInteractor,
    private val commentListArgs: CommentListFragment.Args
) : ViewModel() {

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

    private val mutableError = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = mutableError

    private val mutableNavigation = MutableLiveData<Event<CommentListNavEvent>>()
    val navigation: LiveData<Event<CommentListNavEvent>> = mutableNavigation

    val entityItems: Flow<PagingData<ListItem>>
        get() = paginatedListDelegate.items

    val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = commentListArgs.parentEntityData.fieldSchema.displayName,
            bgColorInt = ColorUtils.getColor(commentListArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true
        )

    fun onBackPressed() {
        mutableNavigation.value = Event(CommentListNavEvent.BackEvent)
    }

    fun onError(error: Exception) {
        mutableError.postValue(Event(error))
    }
}
