package by.krossovochkin.fiberyunofficial.login.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.login.domain.LoginInteractor
import dagger.Module
import dagger.Provides

@Module
object LoginPresentationModule {

    @JvmStatic
    @Provides
    fun loginViewModel(
        fragment: Fragment,
        loginViewModelFactory: LoginViewModelFactory
    ): LoginViewModel {
        return ViewModelProvider(fragment, loginViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun loginViewModelFactory(
        loginInteractor: LoginInteractor
    ): LoginViewModelFactory {
        return LoginViewModelFactory(
            loginInteractor
        )
    }
}
