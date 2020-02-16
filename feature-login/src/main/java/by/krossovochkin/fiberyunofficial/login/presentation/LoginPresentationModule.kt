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
        LoginViewModelFactory: LoginViewModelFactory
    ): LoginViewModel {
        return ViewModelProvider(fragment, LoginViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun loginViewModelFactory(
        getLoginInteractor: LoginInteractor,
        LoginParentListener: LoginViewModel.ParentListener
    ): LoginViewModelFactory {
        return LoginViewModelFactory(
            getLoginInteractor,
            LoginParentListener
        )
    }
}
