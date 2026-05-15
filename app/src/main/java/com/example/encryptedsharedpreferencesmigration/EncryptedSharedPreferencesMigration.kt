package com.example.encryptedsharedpreferencesmigration

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedSharedPreferencesMigration(
    private val context: Context,
) : DataMigration<Preferences> {

    private val encryptedSharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFERENCE_FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    override suspend fun shouldMigrate(currentData: Preferences): Boolean =
        try {
            encryptedSharedPreferences.contains(OLD_KEY)
        } catch (e: Exception) {
            false
        }

    override suspend fun migrate(currentData: Preferences): Preferences {
        val oldValue = encryptedSharedPreferences.getString(OLD_KEY, "") ?: ""
        return currentData.toMutablePreferences().apply {
            this[Preference.TEXT_KEY] = oldValue
        }.toPreferences()
    }

    override suspend fun cleanUp() {
        encryptedSharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val PREFERENCE_FILENAME = "encrypted-preference-sample"
        private const val OLD_KEY = "text"
    }
}
