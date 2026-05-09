package com.krossovochkin.filelist.presentation

import com.krossovochkin.fiberyunofficial.domain.FiberyFileData
import com.krossovochkin.fiberyunofficial.ui.list.ListItem

data class FileListItem(
    val title: String,
    val fileData: FiberyFileData
) : ListItem
