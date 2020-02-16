package by.krossovochkin.login.data

import by.krossovochkin.fiberyunofficial.core.data.auth.AuthStorage
import by.krossovochkin.login.domain.LoginRepository

class LoginRepositoryImpl(
    private val authStorage: AuthStorage
) : LoginRepository {

    override fun saveLogin(account: String, token: String) {
        authStorage.saveAccount(account)
        authStorage.saveToken(token)
    }

    override fun getToken(): String {
        return authStorage.getToken()
    }
}
