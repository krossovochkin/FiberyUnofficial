package by.krossovochkin.fiberyunofficial

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import by.krossovochkin.fiberyunofficial.core.data.auth.AuthStorage

private const val PREFS_NAME = "AUTH"
private const val MASTER_KEY_ALIAS = "fiberyunofficialauth"
private const val KEY_ACCOUNT = "KEY_ACCOUNT"
private const val KEY_TOKEN = "KEY_TOKEN"

class AuthStorageImpl(
    context: Context
) : AuthStorage {

    private val prefs = EncryptedSharedPreferences.create(
        PREFS_NAME,
        MASTER_KEY_ALIAS,
        context.applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private var account: String = prefs.getString(KEY_ACCOUNT, "")!!
    private var token: String = prefs.getString(KEY_TOKEN, "")!!

    override fun saveAccount(account: String) {
        prefs.edit { putString(KEY_ACCOUNT, account) }
        this.account = account
    }

    override fun getAccount(): String {
        return this.account
    }

    override fun saveToken(token: String) {
        prefs.edit { putString(KEY_TOKEN, token) }
        this.token = token
    }

    override fun getToken(): String {
        return this.token
    }
}
