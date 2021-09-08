package com.krossovochkin.filelist.domain

import com.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.core.domain.FiberyFileData
import com.krossovochkin.fiberyunofficial.core.domain.ParentEntityData

interface GetFileListInteractor {

    suspend fun execute(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData,
        offset: Int,
        pageSize: Int
    ): List<FiberyFileData>
}

class GetFileListInteractorImpl(
    private val fileListRepository: FileListRepository
) : GetFileListInteractor {

    override suspend fun execute(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData,
        offset: Int,
        pageSize: Int
    ): List<FiberyFileData> {
        return fileListRepository.getFileList(entityType, parentEntityData, offset, pageSize)
    }
}
