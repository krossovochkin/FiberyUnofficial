package by.krossovochkin.fiberyunofficial.core.domain

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.Date

@Parcelize
data class FiberyAppData(
    val name: String
) : Parcelable

@Parcelize
data class FiberyEntityTypeSchema(
    val name: String,
    val fields: List<FiberyFieldSchema>,
    val meta: FiberyEntityTypeMetaData
) : Parcelable {

    @IgnoredOnParcel
    val displayName: String = name.substringAfter("/")
}

@Parcelize
data class FiberyEntityTypeMetaData(
    val uiColorHex: String,
    val isDomain: Boolean,
    val isPrimitive: Boolean,
    val isEnum: Boolean
) : Parcelable

@Parcelize
data class FiberyFieldSchema(
    val name: String,
    val type: String,
    val meta: FiberyFieldMetaData
) : Parcelable

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
        val value: Date?,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Parcelize
    data class SingleSelectFieldData(
        val title: String,
        val value: String?,
        val values: List<SingleSelectItemData>,
        override val schema: FiberyFieldSchema
    ) : FieldData()

    @Parcelize
    data class SingleSelectItemData(
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
