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
package com.krossovochkin.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val DATASTORE_NAME = "AUTH"

private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class AuthStorage @Inject constructor(
    @ApplicationContext context: Context,
    private val encryptionService: EncryptionService,
) {
    private val dataStore = context.dataStore

    private val accountKey = stringPreferencesKey("encrypted_account")
    private val tokenKey = stringPreferencesKey("encrypted_token")

    private var account: String = ""
    private var token: String = ""

    suspend fun saveLogin(account: String, token: String): Boolean {
        if (!account.isValidAccount()) return false
        if (!token.isValidToken()) return false

        dataStore.edit { prefs ->
            prefs[accountKey] = encryptionService.encrypt(account)
            prefs[tokenKey] = encryptionService.encrypt(token)
        }
        this.account = account
        this.token = token

        return true
    }

    suspend fun loadLogin(): Boolean {
        val prefs = dataStore.data
            .first()

        this.account = encryptionService.decrypt(prefs[accountKey].orEmpty())
        this.token = encryptionService.decrypt(prefs[tokenKey].orEmpty())

        return this.account.isValidAccount() && this.token.isValidToken()
    }

    fun getAccount(): String? {
        return this.account.takeIf { it.isValidAccount() }
    }

    fun getToken(): String? {
        return this.token.takeIf { it.isValidToken() }
    }

    private fun String.isValidAccount(): Boolean {
        return this.isNotEmpty()
    }

    private fun String.isValidToken(): Boolean {
        return this.isNotEmpty()
    }
}
