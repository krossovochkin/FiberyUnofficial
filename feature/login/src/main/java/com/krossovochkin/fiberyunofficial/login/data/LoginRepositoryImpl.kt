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
package com.krossovochkin.fiberyunofficial.login.data

import com.krossovochkin.auth.AuthStorage
import com.krossovochkin.fiberyunofficial.login.domain.LoginRepository

class LoginRepositoryImpl(
    private val authStorage: AuthStorage
) : LoginRepository {

    override fun saveLogin(account: String, token: String) {
        authStorage.saveLogin(account = account, token = token)
    }

    override fun getToken(): String {
        return authStorage.getToken()
    }
}
