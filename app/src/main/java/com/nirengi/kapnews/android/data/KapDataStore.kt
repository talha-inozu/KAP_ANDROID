package com.nirengi.kapnews.android.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.kapDataStore: DataStore<Preferences> by preferencesDataStore(name = "kap_settings")

object KapPrefsKeys {
    val jwt = stringPreferencesKey("jwt")
    val pushEnabled = booleanPreferencesKey("push_enabled")
}
