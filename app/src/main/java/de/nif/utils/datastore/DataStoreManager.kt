package de.nif.utils.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreManager(
    private val dataStore: DataStore<Preferences>
) {

    val preferenceFlow = dataStore.data

    fun <T> getPreferenceFlow(
        key: Preferences.Key<T>,
        default: T
    ): Flow<T> {

        return dataStore.data
            .map { prefs ->
                prefs[key] ?: default
            }
    }

    suspend fun <T> getPreference(
        key: Preferences.Key<T>,
        default: T
    ): T {
        return dataStore.data
            .map { prefs ->
                prefs[key] ?: default
            }
            .first()
    }

    suspend fun <T> editPreference(
        key : Preferences.Key<T>,
        value: T
    ) {
        dataStore.edit { prefs ->
            prefs[key] = value
        }
    }

}