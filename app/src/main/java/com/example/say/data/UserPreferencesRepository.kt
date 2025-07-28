package com.example.say.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class Language(val code: String) {
    TURKISH("tr"),
    ENGLISH("en")
}

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM
}

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val theme: Flow<Theme> = context.dataStore.data
        .map {
            Theme.valueOf(
                it[PreferencesKeys.THEME] ?: Theme.SYSTEM.name
            )
        }

        val language: Flow<Language> = context.dataStore.data
        .map {
            val langCode = it[PreferencesKeys.LANGUAGE] ?: Language.TURKISH.code
            Language.values().firstOrNull { l -> l.code == langCode } ?: Language.TURKISH
        }

    suspend fun setLanguage(language: Language) {
        context.dataStore.edit {
            it[PreferencesKeys.LANGUAGE] = language.code
        }
    }

    suspend fun setTheme(theme: Theme) {
        context.dataStore.edit {
            it[PreferencesKeys.THEME] = theme.name
        }
    }
}
