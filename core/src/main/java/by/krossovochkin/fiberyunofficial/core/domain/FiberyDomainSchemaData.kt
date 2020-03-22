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
package by.krossovochkin.fiberyunofficial.core.domain

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDateTime
import java.math.BigDecimal

@Parcelize
data class FiberyAppData(
    val name: String
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class FiberyEntityTypeSchema(
    val name: String,
    val fields: List<FiberyFieldSchema>,
    val meta: FiberyEntityTypeMetaData
) : Parcelable {

    @IgnoredOnParcel
    val displayName: String = name.substringAfter("/")
}

@JsonClass(generateAdapter = true)
@Parcelize
data class FiberyEntityTypeMetaData(
    val uiColorHex: String,
    val isDomain: Boolean,
    val isPrimitive: Boolean,
    val isEnum: Boolean
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class FiberyFieldSchema(
    val name: String,
    val type: String,
    val meta: FiberyFieldMetaData
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class FiberyFieldMetaData(
    val isUiTitle: Boolean,
    val relationId: String?,
    val isCollection: Boolean,
    val uiOrder: Int
) : Parcelable {

    @IgnoredOnParcel
    val isRelation: Boolean = relationId != null
}

@Parcelize
class FiberyEntityData(
    val id: String,
    val publicId: String,
    val title: String,
    val schema: FiberyEntityTypeSchema
) : Parcelable

@Parcelize
data class FiberyEntityDetailsData(
    val id: String,
    val publicId: String,
    val title: String,
    val fields: List<FieldData>,
    val schema: FiberyEntityTypeSchema
) : Parcelable

sealed class FieldData : Parcelable {

    abstract val schema: FiberyFieldSchema

    @Parcelize
    data class TextFieldData(
        val title: String,
        val value: String?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Parcelize
    data class NumberFieldData(
        val title: String,
        val value: BigDecimal?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Parcelize
    data class CheckboxFieldData(
        val title: String,
        val value: Boolean?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Parcelize
    data class DateTimeFieldData(
        val title: String,
        val value: LocalDateTime?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Parcelize
    data class SingleSelectFieldData(
        val title: String,
        val selectedValue: EnumItemData?,
        val values: List<EnumItemData>,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Parcelize
    data class MultiSelectFieldData(
        val title: String,
        val selectedValues: List<EnumItemData>,
        val values: List<EnumItemData>,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Parcelize
    data class EnumItemData(
        val id: String,
        val title: String
    ) : Parcelable

    @Parcelize
    data class RichTextFieldData(
        val title: String,
        val value: String?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Parcelize
    data class RelationFieldData(
        val title: String,
        val fiberyEntityData: FiberyEntityData?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Parcelize
    data class CollectionFieldData(
        val title: String,
        val count: Int,
        val entityTypeSchema: FiberyEntityTypeSchema,
        val entityData: FiberyEntityData,
        override val schema: FiberyFieldSchema
    ) : FieldData()
}
