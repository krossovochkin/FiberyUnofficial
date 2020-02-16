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

    fun loginParentListener(): LoginViewModel.ParentListener
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

