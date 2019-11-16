package by.krossovochkin.fiberyunofficial.core.data.api

object FiberyApiConstants {

    enum class Type(
        val value: String
    ) {
        USER("fibery/user")
    }

    enum class Field(
        val value: String
    ) {
        ID("fibery/id"),
        COLLABORATION_DOCUMENT("Collaboration~Documents/Document")
    }

    enum class Operator(
        val value: String
    ) {
        EQUALS("=")
    }

    const val DELIMITER_APP_TYPE = "/"
}
