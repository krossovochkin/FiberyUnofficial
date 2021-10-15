package com.krossovochkin.filelist.data

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.getSystemService
import com.krossovochkin.auth.AuthStorage
import com.krossovochkin.fiberyunofficial.api.FiberyApiConstants
import com.krossovochkin.fiberyunofficial.api.FiberyServiceApi
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommand
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandArgsDto
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandArgsQueryDto
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandBody
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFileData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import com.krossovochkin.filelist.domain.FileListRepository

private const val PARAM_ID = "\$id"

class FileListRepositoryImpl(
    private val context: Context,
    private val authStorage: AuthStorage,
    private val fiberyServiceApi: FiberyServiceApi
) : FileListRepository {

    override suspend fun getFileList(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData,
        offset: Int,
        pageSize: Int
    ): List<FiberyFileData> {
        val fileName = FiberyApiConstants.Field.NAME.value
        val fileSecret = FiberyApiConstants.Field.SECRET.value
        val idType = FiberyApiConstants.Field.ID.value

        val dto = fiberyServiceApi.getEntities(
            listOf(
                FiberyCommandBody(
                    command = FiberyCommand.QUERY_ENTITY.value,
                    args = FiberyCommandArgsDto(
                        query = FiberyCommandArgsQueryDto(
                            from = parentEntityData.parentEntity.schema.name,
                            select = mapOf(
                                parentEntityData.fieldSchema.name to FiberyCommandArgsQueryDto(
                                    from = parentEntityData.fieldSchema.name,
                                    select = listOf(
                                        fileName,
                                        fileSecret,
                                        idType
                                    ),
                                    offset = offset,
                                    limit = pageSize
                                )
                            ),
                            where = listOf(
                                FiberyApiConstants.Operator.EQUALS.value,
                                listOf(FiberyApiConstants.Field.ID.value),
                                PARAM_ID
                            ),
                            limit = 1
                        ),
                        params = mapOf(PARAM_ID to parentEntityData.parentEntity.id)
                    )
                )
            )
        ).first()

        @Suppress("UNCHECKED_CAST")
        val result = dto.result as List<Map<String, List<Map<String, Any>>>>
        return result.first()
            .flatMap { it.value }
            .map {
                val map = it
                val title = requireNotNull(map[fileName]) { "fileName is missing" } as String
                val secret = requireNotNull(map[fileSecret]) { "fileSecret is missing" } as String
                val id = requireNotNull(map[idType]) { "id is missing" } as String
                FiberyFileData(
                    id = id,
                    secret = secret,
                    title = title,
                    schema = entityType
                )
            }
    }

    override suspend fun downloadFile(
        data: FiberyFileData
    ) {
        context.getSystemService<DownloadManager>()!!
            .enqueue(
                DownloadManager
                    .Request(
                        Uri.parse("https://${authStorage.getAccount()}.fibery.io/api/files/${data.secret}")
                    )
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .addRequestHeader("Authorization", "Token ${authStorage.getToken()}")
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, data.title)
                    .setTitle(data.title)
            )
    }
}
