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
package com.krossovochkin.fiberyunofficial.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krossovochkin.fiberyunofficial.login.domain.LoginInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginInteractor: LoginInteractor
) : ViewModel() {

    private val navigationChannel = Channel<LoginNavEvent>(Channel.BUFFERED)
    val navigation: Flow<LoginNavEvent>
        get() = navigationChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            if (loginInteractor.isLoggedIn()) {
                onLoginSuccess()
            }
        }
    }

    fun login(account: String, token: String) {
        viewModelScope.launch {
            val isSuccessful = loginInteractor.login(account, token)
            if (isSuccessful) {
                onLoginSuccess()
            }
        }
    }

    private fun onLoginSuccess() {
        viewModelScope.launch {
            navigationChannel.send(LoginNavEvent.OnLoginSuccessEvent)
        }
    }
}
