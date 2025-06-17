package com.krossovochkin.filelist.domain

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.krossovochkin.auth.AuthStorage
import com.krossovochkin.fiberyunofficial.domain.FiberyFileData
import javax.inject.Inject

class DownloadFileInteractor @Inject constructor(
    private val context: Context,
    private val authStorage: AuthStorage,
) {

    suspend fun execute(
        data: FiberyFileData
    ) {
        context.getSystemService<DownloadManager>()!!
            .enqueue(
                DownloadManager
                    .Request(
                        "https://${authStorage.getAccount()}.fibery.io/api/files/${data.secret}".toUri()
                    )
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .addRequestHeader("Authorization", "Token ${authStorage.getToken()}")
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, data.title)
                    .setTitle(data.title)
            )
    }
}
