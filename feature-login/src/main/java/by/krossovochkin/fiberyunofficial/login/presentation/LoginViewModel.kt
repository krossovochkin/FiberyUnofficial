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
package by.krossovochkin.fiberyunofficial.login.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.login.domain.LoginInteractor

class LoginViewModel(
    private val loginInteractor: LoginInteractor
) : ViewModel() {

    private val mutableNavigation = MutableLiveData<Event<LoginNavEvent>>()
    val navigation: LiveData<Event<LoginNavEvent>> = mutableNavigation

    init {
        if (loginInteractor.isLoggedIn()) {
            mutableNavigation.value = Event(LoginNavEvent.OnLoginSuccessEvent)
        }
    }

    fun login(account: String, token: String) {
        val isSuccessful = loginInteractor.login(account, token)
        if (isSuccessful) {
            mutableNavigation.value = Event(LoginNavEvent.OnLoginSuccessEvent)
        }
    }
}
