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
        NUMBER_INT("fibery/int"),
        NUMBER_DECIMAL("fibery/decimal"),
        CHECKBOX("fibery/bool"),
        URL("fibery/url"),
        EMAIL("fibery/email")
    }

    enum class Field(
        val value: String
    ) {
        ID("fibery/id"),
        PUBLIC_ID("fibery/public-id"),
        NAME("fibery/name"),
        ENUM_NAME("enum/name"),
        DOCUMENT_SECRET("Collaboration~Documents/secret")
    }

    enum class Operator(
        val value: String
    ) {
        EQUALS("=")
    }

    enum class Limit(
        val value: String
    ) {
        NO_LIMIT("q/no-limit")
    }

    const val DELIMITER_APP_TYPE = "/"
}
