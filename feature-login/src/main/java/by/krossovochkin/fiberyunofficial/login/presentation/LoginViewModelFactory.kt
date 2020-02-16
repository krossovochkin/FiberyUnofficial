package by.krossovochkin.fiberyunofficial.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.krossovochkin.fiberyunofficial.login.domain.LoginInteractor

class LoginViewModelFactory(
    private val loginInteractor: LoginInteractor,
    private val loginParentListener: LoginViewModel.ParentListener
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == LoginViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            LoginViewModel(
                loginInteractor,
                loginParentListener
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
