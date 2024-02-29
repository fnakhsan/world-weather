package com.fnakhsan.core.data.source.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherDatastore @Inject constructor(private val dataStore: DataStore<Preferences>) {
    fun getLocation(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[LOCATION_KEY]
        }
    }

    suspend fun saveLocation(location: String) {
        dataStore.edit { preferences ->
            preferences[LOCATION_KEY] = location
        }
    }

    suspend fun clearLocation() {
        dataStore.edit { preferences ->
            preferences.remove(LOCATION_KEY)
        }
    }

    companion object {
        private val LOCATION_KEY = stringPreferencesKey("location")
    }
}