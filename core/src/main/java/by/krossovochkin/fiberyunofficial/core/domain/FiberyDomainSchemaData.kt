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
    val isUiTitle: Boolean
) : Parcelable

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
    val fields: Map<String, @RawValue Any>,
    val schema: FiberyEntityTypeSchema
) : Parcelable
