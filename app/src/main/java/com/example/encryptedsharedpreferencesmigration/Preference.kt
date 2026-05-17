package com.example.encryptedsharedpreferencesmigration

import android.content.Context
import android.content.SharedPreferences

class Preference(context: Context) {
    private val cryptoManager = CryptoManager(context)
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_FILENAME, Context.MODE_PRIVATE)

    init {
        EncryptedSharedPreferencesMigration(context).migrateIfNeeded(sharedPreferences, cryptoManager)
    }

    fun save(value: String) {
        sharedPreferences.edit().putString(KEY, cryptoManager.encrypt(value)).apply()
    }

    fun load(): String {
        val stored = sharedPreferences.getString(KEY, null) ?: return ""
        return runCatching { cryptoManager.decrypt(stored) }.getOrDefault("")
    }

    companion object {
        private const val KEY = "text"
        private const val PREFERENCE_FILENAME = "preference"
    }
}
