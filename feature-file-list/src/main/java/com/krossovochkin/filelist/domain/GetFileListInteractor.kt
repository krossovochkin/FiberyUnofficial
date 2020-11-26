package com.krossovochkin.filelist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFileData
import by.krossovochkin.fiberyunofficial.core.domain.ParentEntityData

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
