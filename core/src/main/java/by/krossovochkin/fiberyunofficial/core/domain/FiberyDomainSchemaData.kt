package by.krossovochkin.fiberyunofficial.core.domain

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class FiberyAppData(
    val name: String
) : Parcelable

@Parcelize
data class FiberyEntityTypeSchema(
    val name: String,
    val fields: List<FiberyFieldSchema>,
    val uiColorHex: String
) : Parcelable {

    @IgnoredOnParcel
    val displayName: String = name.substringAfter("/")
}

@Parcelize
data class FiberyFieldSchema(
    val name: String,
    val type: String,
    val meta: FiberyFieldMetaData
) : Parcelable

@Parcelize
data class FiberyFieldMetaData(
    val isUiTitle: Boolean,
    val isCollection: Boolean,
    val isRelation: Boolean
) : Parcelable

@Parcelize
data class FiberyEntityData(
    val data: Map<String, @RawValue Any>,
    val schema: FiberyEntityTypeSchema
) : Parcelable {

    @IgnoredOnParcel
    val title: String by lazy {
        val fieldName = schema.fields.find { it.meta.isUiTitle }!!.name
        data[fieldName] as String
    }
}
