package by.krossovochkin.fiberyunofficial.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.krossovochkin.fiberyunofficial.login.domain.LoginInteractor

class LoginViewModelFactory(
    private val loginInteractor: LoginInteractor
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == LoginViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            LoginViewModel(
                loginInteractor
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
