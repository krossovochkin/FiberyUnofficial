package com.krossovochkin.auth

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val KEYSET_NAME = "fiberyunofficialauth"
private const val PREF_NAME = "AUTH"
private const val MASTER_KEY_URI = "android-keystore://auth_master_key"

class EncryptionService @Inject constructor(
    @ApplicationContext context: Context
) {

    init {
        AeadConfig.register()
    }

    private val aead = AndroidKeysetManager.Builder()
        .withSharedPref(context, KEYSET_NAME, PREF_NAME)
        .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
        .withMasterKeyUri(MASTER_KEY_URI)
        .build()
        .keysetHandle
        .getPrimitive(Aead::class.java)

    fun encrypt(input: String): String {
        val encryptedBytes = aead.encrypt(input.toByteArray(), null)
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }

    fun decrypt(encrypted: String?): String {
        if (encrypted.isNullOrEmpty()) return ""
        val encryptedBytes = Base64.decode(encrypted, Base64.NO_WRAP)
        return String(aead.decrypt(encryptedBytes, null))
    }
}
