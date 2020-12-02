package com.krossovochkin.commentlist.presentation

import by.krossovochkin.fiberyunofficial.core.domain.FiberyCommentData
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem

data class CommentListItem(
    val authorName: String,
    val createDate: String,
    val text: String,
    val commentData: FiberyCommentData
) : ListItem
