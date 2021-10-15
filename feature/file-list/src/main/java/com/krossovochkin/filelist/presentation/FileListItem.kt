package com.krossovochkin.filelist.presentation

import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.fiberyunofficial.core.domain.FiberyFileData

data class FileListItem(
    val title: String,
    val fileData: FiberyFileData
) : ListItem
