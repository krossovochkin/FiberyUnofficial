package com.krossovochkin.filelist.domain

import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFileData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData

interface FileListRepository {

    suspend fun getFileList(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData,
        offset: Int,
        pageSize: Int
    ): List<FiberyFileData>

    suspend fun downloadFile(data: FiberyFileData)
}
