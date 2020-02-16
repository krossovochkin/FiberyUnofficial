package by.krossovochkin.fiberyunofficial.login.domain

interface LoginInteractor {

    fun login(account: String, token: String): Boolean

    fun isLoggedIn(): Boolean
}

class LoginInteractorImpl(
    private val loginRepository: LoginRepository
) : LoginInteractor {

    override fun login(account: String, token: String): Boolean {
        if (account.isValidAccount() && !token.isValidToken()) {
            return false
        }

        loginRepository.saveLogin(account, token)

        return true
    }

    override fun isLoggedIn(): Boolean {
        return loginRepository.getToken().isValidToken()
    }

    private fun String.isValidAccount(): Boolean {
        return this.isNotEmpty()
    }

    private fun String.isValidToken(): Boolean {
        return this.isNotEmpty()
    }
}
