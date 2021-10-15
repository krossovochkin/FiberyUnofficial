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
package com.krossovochkin.fiberyunofficial.di.login

import com.krossovochkin.auth.AuthStorage
import com.krossovochkin.fiberyunofficial.GlobalDependencies
import com.krossovochkin.fiberyunofficial.login.presentation.LoginViewModelFactory
import dagger.Component
import javax.inject.Scope

interface LoginParentComponent : GlobalDependencies {

    fun authStorage(): AuthStorage
}

@Login
@Component(
    modules = [
        LoginDataModule::class,
        LoginDomainModule::class,
        LoginPresentationModule::class
    ],
    dependencies = [LoginParentComponent::class]
)
interface LoginComponent {

    fun viewModelFactoryProducer(): () -> LoginViewModelFactory

    @Component.Factory
    interface Factory {

        fun create(
            loginParentComponent: LoginParentComponent
        ): LoginComponent
    }
}

@Scope
@Retention
annotation class Login
