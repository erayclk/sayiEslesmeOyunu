package com.example.say.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScoreRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("game_scores", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getScores(): List<Score> {
        val json = sharedPreferences.getString("scores_list", null) ?: return emptyList()
        val type = object : TypeToken<List<Score>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveScore(score: Score) {
        val scores = getScores().toMutableList()
        scores.add(score)
        val json = gson.toJson(scores)
        sharedPreferences.edit().putString("scores_list", json).apply()
    }

    fun clearScores() {
        sharedPreferences.edit().remove("scores_list").apply()
    }
}
