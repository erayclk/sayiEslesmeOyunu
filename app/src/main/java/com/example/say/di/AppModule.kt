package com.example.say.di

import android.content.Context
import com.example.say.data.ScoreRepository
import com.example.say.data.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideScoreRepository(@ApplicationContext context: Context): ScoreRepository {
        // This assumes ScoreRepository needs the application context.
        // If it has other dependencies, they need to be provided here as well.
        return ScoreRepository(context)
    }
}
