package com.krossovochkin.commentlist.presentation

import com.krossovochkin.fiberyunofficial.core.domain.FiberyCommentData
import com.krossovochkin.fiberyunofficial.core.presentation.ListItem

data class CommentListItem(
    val authorName: String,
    val createDate: String,
    val text: String,
    val commentData: FiberyCommentData
) : ListItem
