package com.example.say

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.say.data.Language
import com.example.say.data.Theme
import com.example.say.data.ScoreRepository
import com.example.say.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    val theme: StateFlow<Theme> = userPreferencesRepository.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Theme.SYSTEM
        )

    val language: StateFlow<Language> = userPreferencesRepository.language
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Language.TURKISH
        )

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguage(language)
            // Apply locale immediately so UI reflects language change without app restart
            val locale = java.util.Locale(language.code)
            val localeList = androidx.core.os.LocaleListCompat.forLanguageTags(locale.toLanguageTag())
            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(localeList)
        }
    }

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            userPreferencesRepository.setTheme(theme)
        }
    }

    fun deleteAllScores() {
        viewModelScope.launch {
            scoreRepository.clearScores()
        }
    }
}


