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
        listOf(EncryptedSharedPreferencesMigration(context))
    },
)

class Preference(context: Context) {
    private val dataStore = context.dataStore

    val textFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[TEXT_KEY] ?: ""
    }

    suspend fun save(value: String) {
        dataStore.edit { prefs ->
            prefs[TEXT_KEY] = value
        }
    }

    companion object {
        internal val TEXT_KEY = stringPreferencesKey("text")
    }
}
