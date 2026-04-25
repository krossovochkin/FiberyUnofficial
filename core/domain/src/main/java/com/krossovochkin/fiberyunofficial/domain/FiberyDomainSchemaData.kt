/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.krossovochkin.fiberyunofficial.domain

import android.annotation.SuppressLint
import android.os.Parcelable
import com.krossovochkin.serialization.BigDecimalSerializer
import com.krossovochkin.serialization.LocalDateSerializer
import com.krossovochkin.serialization.LocalDateTimeSerializer
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.math.BigDecimal
import java.util.Locale

@Serializable
@Parcelize
data class FiberyAppData(
    val name: String
) : Parcelable

@Serializable
@JsonClass(generateAdapter = true)
@Parcelize
data class FiberyEntityTypeSchema(
    val name: String,
    val fields: List<FiberyFieldSchema>,
    val meta: FiberyEntityTypeMetaData
) : Parcelable {

    @IgnoredOnParcel
    val appName: String = name.substringBefore("/")

    @IgnoredOnParcel
    val displayName: String = name.substringAfterLast("/")
}

@Serializable
@JsonClass(generateAdapter = true)
@Parcelize
data class FiberyEntityTypeMetaData(
    val uiColorHex: String,
    val isDomain: Boolean,
    val isPrimitive: Boolean,
    val isEnum: Boolean
) : Parcelable

@Serializable
@JsonClass(generateAdapter = true)
@Parcelize
data class FiberyEntitySortData(
    val items: List<Item>
) : Parcelable {

    @Serializable
    @JsonClass(generateAdapter = true)
    @Parcelize
    data class Item(
        val field: FiberyFieldSchema,
        val condition: Condition,
    ) : Parcelable {

        @Serializable
        @JsonClass(generateAdapter = false)
        enum class Condition(
            val value: String
        ) {
            ASCENDING(value = "q/asc"),
            DESCENDING(value = "q/desc")
        }
    }
}

@Serializable
@JsonClass(generateAdapter = true)
@Parcelize
data class FiberyEntityFilterData(
    val mergeType: MergeType,
    val items: List<Item>
) : Parcelable {

    @Serializable
    @JsonClass(generateAdapter = false)
    enum class MergeType(
        val value: String
    ) {
        ALL("and"),
        ANY("or")
    }

    @Serializable
    sealed class Item(
        val type: Type
    ) : Parcelable {
        abstract val field: FiberyFieldSchema
        abstract val condition: Condition

        @Serializable
        @JsonClass(generateAdapter = false)
        enum class Type {
            SINGLE_SELECT
        }

        @Serializable
        @JsonClass(generateAdapter = false)
        enum class Condition(
            val value: String
        ) {
            EQUALS("="),
            NOT_EQUALS("!=")
        }

        @Serializable
        @JsonClass(generateAdapter = true)
        @Parcelize
        data class SingleSelectItem(
            override val field: FiberyFieldSchema,
            override val condition: Condition,
            val param: FieldData.EnumItemData
        ) : Item(Type.SINGLE_SELECT)
    }
}

@Serializable
@JsonClass(generateAdapter = true)
@Parcelize
data class FiberyFieldSchema(
    val name: String,
    val type: String,
    val meta: FiberyFieldMetaData
) : Parcelable {

    @IgnoredOnParcel
    val displayName: String
        @SuppressLint("DefaultLocale")
        get() = name.substringAfterLast("/")
            .split("-")
            .joinToString(separator = " ") { name ->
                name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }
}

@Serializable
@JsonClass(generateAdapter = true)
@Parcelize
data class FiberyFieldMetaData(
    val isUiTitle: Boolean,
    val relationId: String?,
    val isCollection: Boolean,
    val uiOrder: Int,
    val numberUnit: String?,
    val numberPrecision: Int
) : Parcelable {

    @IgnoredOnParcel
    val isRelation: Boolean = relationId != null
}

@Serializable
@Parcelize
data class FiberyEntityData(
    val id: String,
    val publicId: String,
    val title: String,
    val schema: FiberyEntityTypeSchema
) : Parcelable

@Serializable
@Parcelize
data class FiberyFileData(
    val id: String,
    val title: String,
    val secret: String,
    val schema: FiberyEntityTypeSchema
) : Parcelable

@Serializable
@Parcelize
data class FiberyCommentData(
    val id: String,
    val authorName: String,
    val text: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createDate: LocalDateTime,
    val schema: FiberyEntityTypeSchema
) : Parcelable

@Serializable
@Parcelize
data class FiberyEntityDetailsData(
    val id: String,
    val publicId: String,
    val title: String,
    val fields: List<FieldData>,
    val schema: FiberyEntityTypeSchema
) : Parcelable

@Serializable
@Parcelize
data class ParentEntityData(
    val fieldSchema: FiberyFieldSchema,
    val parentEntity: FiberyEntityData
) : Parcelable

@Serializable
sealed class FieldData : Parcelable {

    abstract val schema: FiberyFieldSchema

    @Serializable
    @Parcelize
    data class TextFieldData(
        val title: String,
        val value: String?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Serializable
    @Parcelize
    data class UrlFieldData(
        val title: String,
        val value: String?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Serializable
    @Parcelize
    data class EmailFieldData(
        val title: String,
        val value: String?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Serializable
    @Parcelize
    data class NumberFieldData(
        val title: String,
        @Serializable(with = BigDecimalSerializer::class)
        val value: BigDecimal?,
        val unit: String?,
        val precision: Int,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Serializable
    @Parcelize
    data class CheckboxFieldData(
        val title: String,
        val value: Boolean?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Serializable
    @Parcelize
    data class DateFieldData(
        val title: String,
        @Serializable(with = LocalDateSerializer::class)
        val value: LocalDate?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Serializable
    @Parcelize
    data class DateTimeFieldData(
        val title: String,
        @Serializable(with = LocalDateTimeSerializer::class)
        val value: LocalDateTime?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Serializable
    @Parcelize
    data class DateRangeFieldData(
        val title: String,
        @Serializable(with = LocalDateSerializer::class)
        val start: LocalDate?,
        @Serializable(with = LocalDateSerializer::class)
        val end: LocalDate?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Serializable
    @Parcelize
    data class DateTimeRangeFieldData(
        val title: String,
        @Serializable(with = LocalDateTimeSerializer::class)
        val start: LocalDateTime?,
        @Serializable(with = LocalDateTimeSerializer::class)
        val end: LocalDateTime?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Serializable
    @Parcelize
    data class SingleSelectFieldData(
        val title: String,
        val selectedValue: EnumItemData?,
        val values: List<EnumItemData>,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Serializable
    @Parcelize
    data class MultiSelectFieldData(
        val title: String,
        val selectedValues: List<EnumItemData>,
        val values: List<EnumItemData>,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Serializable
    @JsonClass(generateAdapter = true)
    @Parcelize
    data class EnumItemData(
        val id: String,
        val title: String
    ) : Parcelable

    @Serializable
    @Parcelize
    data class RichTextFieldData(
        val title: String,
        val value: String?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Serializable
    @Parcelize
    data class RelationFieldData(
        val title: String,
        val fiberyEntityData: FiberyEntityData?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Serializable
    @Parcelize
    data class CollectionFieldData(
        val title: String,
        val count: Int,
        val entityTypeSchema: FiberyEntityTypeSchema,
        override val schema: FiberyFieldSchema
    ) : FieldData()
}
