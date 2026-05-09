package com.krossovochkin.commentlist.presentation

import com.krossovochkin.fiberyunofficial.domain.FiberyCommentData
import com.krossovochkin.fiberyunofficial.ui.list.ListItem

data class CommentListItem(
    val authorName: String,
    val createDate: String,
    val text: String,
    val commentData: FiberyCommentData
) : ListItem
