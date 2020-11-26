package com.krossovochkin.filelist.presentation

import by.krossovochkin.fiberyunofficial.core.domain.FiberyFileData
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem

data class FileListItem(
    val title: String,
    val fileData: FiberyFileData
) : ListItem
