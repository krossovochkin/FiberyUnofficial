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
package by.krossovochkin.fiberyunofficial.login

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.core.data.auth.AuthStorage
import by.krossovochkin.fiberyunofficial.login.data.LoginDataModule
import by.krossovochkin.fiberyunofficial.login.domain.LoginDomainModule
import by.krossovochkin.fiberyunofficial.login.presentation.LoginFragment
import by.krossovochkin.fiberyunofficial.login.presentation.LoginPresentationModule
import by.krossovochkin.fiberyunofficial.login.presentation.LoginViewModel
import dagger.BindsInstance
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

    fun authStorage(): AuthStorage

    fun loginViewModel(): LoginViewModel

    fun inject(fragment: LoginFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        fun loginGlobalDependencies(loginParentComponent: LoginParentComponent): Builder

        fun build(): LoginComponent
    }
}

@Scope
@Retention
annotation class Login
