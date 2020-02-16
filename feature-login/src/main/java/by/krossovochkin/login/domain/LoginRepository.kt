package by.krossovochkin.login.domain

interface LoginRepository {

    fun saveLogin(account: String, token: String)

    fun getToken(): String
}
