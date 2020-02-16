package by.krossovochkin.login.presentation

import androidx.lifecycle.ViewModel
import by.krossovochkin.login.domain.LoginInteractor

class LoginViewModel(
    private val loginInteractor: LoginInteractor,
    private val loginParentListener: ParentListener
) : ViewModel() {

    init {
        if (loginInteractor.isLoggedIn()) {
            loginParentListener.onLoginSuccess()
        }
    }

    fun login(account: String, token: String) {
        val isSuccessful = loginInteractor.login(account, token)
        if (isSuccessful) {
            loginParentListener.onLoginSuccess()
        }
    }

    interface ParentListener {

        fun onLoginSuccess()
    }
}
