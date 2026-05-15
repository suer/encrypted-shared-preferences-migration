@file:Suppress("DEPRECATION")

package com.example.encryptedsharedpreferencesmigration

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.File

class EncryptedSharedPreferencesMigration(private val context: Context) {

    fun migrateIfNeeded(destination: SharedPreferences, cryptoManager: CryptoManager) {
        if (!espFileExists()) return
        val espPrefs = try { createEsp() } catch (e: Exception) { return }
        val oldValue = espPrefs.getString(OLD_KEY, null) ?: return
        destination.edit().putString(OLD_KEY, cryptoManager.encrypt(oldValue)).apply()
        espPrefs.edit().clear().apply()
    }

    private fun espFileExists(): Boolean {
        val file = File("${context.filesDir.parent}/shared_prefs/$ESP_PREFERENCE_FILENAME.xml")
        return file.exists()
    }

    private fun createEsp(): SharedPreferences {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            ESP_PREFERENCE_FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    companion object {
        private const val ESP_PREFERENCE_FILENAME = "encrypted-preference-sample"
        private const val OLD_KEY = "text"
    }
}
