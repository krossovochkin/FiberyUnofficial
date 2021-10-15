package com.krossovochkin.filelist.domain

import com.krossovochkin.fiberyunofficial.domain.FiberyFileData

interface DownloadFileInteractor {

    suspend fun execute(data: FiberyFileData)
}

class DownloadFileInteractorImpl(
    private val fileListRepository: FileListRepository
) : DownloadFileInteractor {

    override suspend fun execute(data: FiberyFileData) {
        return fileListRepository.downloadFile(data)
    }
}
