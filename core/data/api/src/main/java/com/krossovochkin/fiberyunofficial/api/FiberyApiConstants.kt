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
package com.krossovochkin.fiberyunofficial.api

object FiberyApiConstants {

    enum class Type(
        val value: String
    ) {
        USER("fibery/user"),
        FILE("fibery/file"),
        COMMENT("comments/comment")
    }

    enum class FieldType(
        val value: String
    ) {
        COLLABORATION_DOCUMENT("Collaboration~Documents/Document"),
        TEXT("fibery/text"),
        DATE("fibery/date"),
        DATE_TIME("fibery/date-time"),
        DATE_RANGE("fibery/date-range"),
        DATE_TIME_RANGE("fibery/date-time-range"),
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
        DOCUMENT_SECRET("Collaboration~Documents/secret"),
        START("start"),
        END("end"),
        FILES("Files/Files"),
        SECRET("fibery/secret"),
        CREATION_DATE("fibery/creation-date"),
        COMMENT_AUTHOR("comment/author"),
        COMMENT_SECRET("comment/document-secret"),
        USER_NAME("user/name"),
        DOCUMENTS("documents/documents"),
        WHITEBOARDS("whiteboards/whiteboards")
    }

    enum class Format(
        val value: String
    ) {
        DATE("yyyy-MM-dd"),
        DATE_TIME("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    }

    enum class Operator(
        val value: String
    ) {
        EQUALS("="),
        CONTAINS("q/contains")
    }

    enum class Limit(
        val value: String
    ) {
        NO_LIMIT("q/no-limit")
    }
}
