package com.example.encryptedsharedpreferencesmigration

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "preference",
    produceMigrations = { context ->
        listOf(EncryptedSharedPreferencesMigration(context, CryptoManager()))
    },
)

class Preference(context: Context) {
    private val dataStore = context.dataStore
    private val cryptoManager = CryptoManager()

    val textFlow: Flow<String> = dataStore.data.map { prefs ->
        val stored = prefs[TEXT_KEY]?.takeIf { it.isNotEmpty() } ?: return@map ""
        runCatching { cryptoManager.decrypt(stored) }.getOrDefault("")
    }

    suspend fun save(value: String) {
        dataStore.edit { prefs ->
            prefs[TEXT_KEY] = cryptoManager.encrypt(value)
        }
    }

    companion object {
        internal val TEXT_KEY = stringPreferencesKey("text")
    }
}
