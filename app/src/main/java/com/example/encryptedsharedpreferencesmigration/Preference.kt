package com.example.encryptedsharedpreferencesmigration

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class Preference(context: Context) {
    private val sharedPreferences: SharedPreferences

    init {
        val masterKey =
            MasterKey
                .Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREFERENCE_FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    fun save(value: String) {
        sharedPreferences.edit().putString(KEY, value).apply()
    }

    fun load(): String = sharedPreferences.getString(KEY, "") ?: ""

    companion object {
        private const val KEY = "text"
        private const val PREFERENCE_FILENAME = "encrypted-preference-sample"
    }
}
