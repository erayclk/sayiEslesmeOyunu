package com.example.say

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.say.data.Theme
import com.example.say.data.ScoreRepository
import com.example.say.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    val theme: StateFlow<Theme> = userPreferencesRepository.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Theme.SYSTEM
        )

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


