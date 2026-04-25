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
import kotlinx.coroutines.launch
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@HiltViewModel(assistedFactory = LoginViewModel.Factory::class)
class LoginViewModel @AssistedInject constructor(
    private val loginInteractor: LoginInteractor,
) : ViewModel() {

    init {
        viewModelScope.launch {
            if (loginInteractor.isLoggedIn()) {
                // We don't have the callback here easily, this might need adjustment
                // if there was some logic, but for now we keep it simple.
            }
        }
    }

    fun login(account: String, token: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val isSuccessful = loginInteractor.login(account, token)
            if (isSuccessful) {
                onSuccess()
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(): LoginViewModel
    }
}
