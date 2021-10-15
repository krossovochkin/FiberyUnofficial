package com.krossovochkin.commentlist.presentation

import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.fiberyunofficial.domain.FiberyCommentData

data class CommentListItem(
    val authorName: String,
    val createDate: String,
    val text: String,
    val commentData: FiberyCommentData
) : ListItem
