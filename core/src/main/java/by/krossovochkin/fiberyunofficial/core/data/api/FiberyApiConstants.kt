package by.krossovochkin.fiberyunofficial.core.data.api

object FiberyApiConstants {

    enum class Type(
        val value: String
    ) {
        USER("fibery/user")
    }

    enum class FieldType(
        val value: String
    ) {
        COLLABORATION_DOCUMENT("Collaboration~Documents/Document"),
        TEXT("fibery/text"),
        DATE_TIME("fibery/date-time"),
        UUID("fibery/uuid"),
        NUMBER("fibery/int")
    }

    enum class Field(
        val value: String
    ) {
        ID("fibery/id"),
        PUBLIC_ID("fibery/public-id"),
        ENUM_NAME("enum/name")
    }

    enum class Operator(
        val value: String
    ) {
        EQUALS("=")
    }

    const val DELIMITER_APP_TYPE = "/"
}
