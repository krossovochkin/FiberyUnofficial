package com.krossovochkin.filelist.presentation

import com.krossovochkin.fiberyunofficial.core.domain.FiberyFileData
import com.krossovochkin.fiberyunofficial.core.presentation.ListItem

data class FileListItem(
    val title: String,
    val fileData: FiberyFileData
) : ListItem
