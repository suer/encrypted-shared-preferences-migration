package com.example.encryptedsharedpreferencesmigration

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager

class CryptoManager(context: Context) {
    private val aead: Aead

    init {
        AeadConfig.register()
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, KEYSET_PREFS_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
        aead = keysetHandle.getPrimitive(Aead::class.java)
    }

    fun encrypt(plaintext: String): String {
        val ciphertext = aead.encrypt(plaintext.toByteArray(Charsets.UTF_8), null)
        return Base64.encodeToString(ciphertext, Base64.NO_WRAP)
    }

    fun decrypt(encoded: String): String {
        val ciphertext = Base64.decode(encoded, Base64.NO_WRAP)
        return String(aead.decrypt(ciphertext, null), Charsets.UTF_8)
    }

    companion object {
        private const val KEYSET_NAME = "tink_keyset"
        private const val KEYSET_PREFS_NAME = "tink_keyset_prefs"
        private const val MASTER_KEY_URI = "android-keystore://tink_master_key"
    }
}
