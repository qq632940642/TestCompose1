package com.example.testcompose1.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 创建一个名为 user_preferences 的偏好设置存储文件
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

// DataStore 管理类
class PreferencesManager(context: Context) {
    private val dataStore = context.dataStore

    val darkModeFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DARK_MODE] ?: false
        }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }
}