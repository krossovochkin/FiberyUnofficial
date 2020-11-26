package com.krossovochkin.filelist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFileData
import by.krossovochkin.fiberyunofficial.core.domain.ParentEntityData

interface FileListRepository {

    suspend fun getFileList(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData,
        offset: Int,
        pageSize: Int
    ): List<FiberyFileData>

    suspend fun downloadFile(data: FiberyFileData)
}
