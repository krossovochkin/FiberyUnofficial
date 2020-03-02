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
