package by.krossovochkin.fiberyunofficial.core.data.auth

interface AuthStorage {

    fun saveAccount(account: String)

    fun getAccount(): String

    fun saveToken(token: String)

    fun getToken(): String
}
