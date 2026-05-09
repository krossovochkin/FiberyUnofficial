package com.krossovochkin.fiberyunofficial.navigation

import androidx.navigation3.runtime.NavKey
import com.krossovochkin.fiberyunofficial.domain.FiberyAppData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import kotlinx.serialization.Serializable

@Serializable
object LoginNavKey : NavKey

@Serializable
object AppListNavKey : NavKey

@Serializable
data class EntityTypeListNavKey(
    val fiberyApp: FiberyAppData
) : NavKey

@Serializable
data class EntityListNavKey(
    val entityType: FiberyEntityTypeSchema,
    val parentEntityData: ParentEntityData? = null
) : NavKey

@Serializable
data class EntityDetailsNavKey(
    val entity: FiberyEntityData
) : NavKey

@Serializable
data class EntityCreateNavKey(
    val entityType: FiberyEntityTypeSchema
) : NavKey

@Serializable
data class EntityPickerNavKey(
    val parentEntityData: ParentEntityData,
    val currentEntity: FiberyEntityData? = null
) : NavKey

@Serializable
data class PickerSingleSelectNavKey(
    val item: FieldData.SingleSelectFieldData,
    val parentEntityData: ParentEntityData
) : NavKey

@Serializable
data class PickerMultiSelectNavKey(
    val item: FieldData.MultiSelectFieldData,
    val parentEntityData: ParentEntityData
) : NavKey

@Serializable
data class FileListNavKey(
    val entityType: FiberyEntityTypeSchema,
    val parentEntityData: ParentEntityData
) : NavKey

@Serializable
data class CommentListNavKey(
    val entityType: FiberyEntityTypeSchema,
    val parentEntityData: ParentEntityData
) : NavKey

@Serializable
data class PickerFilterNavKey(
    val entityTypeSchema: FiberyEntityTypeSchema,
    val filter: FiberyEntityFilterData? = null
) : NavKey

@Serializable
data class PickerSortNavKey(
    val entityTypeSchema: FiberyEntityTypeSchema,
    val sort: FiberyEntitySortData? = null
) : NavKey
