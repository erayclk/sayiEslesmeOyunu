package com.example.say

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.say.data.UserPreferencesRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        
        CoroutineScope(Dispatchers.Main).launch {
            val language = userPreferencesRepository.language.first()
            val locale = java.util.Locale(language.code)
            val localeList = LocaleListCompat.forLanguageTags(locale.toLanguageTag())
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }
}
