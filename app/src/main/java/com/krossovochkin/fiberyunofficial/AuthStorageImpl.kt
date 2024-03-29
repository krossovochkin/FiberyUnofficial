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
package com.krossovochkin.fiberyunofficial

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.krossovochkin.auth.AuthStorage

private const val PREFS_NAME = "AUTH"
private const val MASTER_KEY_ALIAS = "fiberyunofficialauth"
private const val KEY_ACCOUNT = "KEY_ACCOUNT"
private const val KEY_TOKEN = "KEY_TOKEN"

class AuthStorageImpl(
    context: Context
) : AuthStorage {

    private val prefs = EncryptedSharedPreferences.create(
        context.applicationContext,
        PREFS_NAME,
        MasterKey.Builder(context.applicationContext, MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private var account: String = prefs.getString(KEY_ACCOUNT, "")!!
    private var token: String = prefs.getString(KEY_TOKEN, "")!!

    override fun saveLogin(account: String, token: String) {
        this.account = account
        this.token = token
        prefs.edit {
            putString(KEY_ACCOUNT, account)
            putString(KEY_TOKEN, token)
        }
    }

    override fun getAccount(): String {
        return this.account
    }

    override fun getToken(): String {
        return this.token
    }
}
