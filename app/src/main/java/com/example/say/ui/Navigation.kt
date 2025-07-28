package com.example.say.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.say.HomeScreen

object Routes {
    const val HOME = "home"
    const val GAME_SETUP = "game_setup"
    const val SCORES = "scores"
    const val SETTINGS = "settings"
    const val GAME_SCREEN = "game_screen/{username}/{difficulty}"

    fun gameScreen(username: String, difficulty: Difficulty): String {
        return "game_screen/$username/${difficulty.name}"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToGameSetup = { navController.navigate(Routes.GAME_SETUP) },
                onNavigateToScores = { navController.navigate(Routes.SCORES) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.GAME_SETUP) {
            GameSetupScreen(onStartGame = { username, difficulty ->
                navController.navigate(Routes.gameScreen(username, difficulty))
            })
        }
        composable(
            route = Routes.GAME_SCREEN,
            arguments = listOf(
                navArgument("username") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val difficultyString = backStackEntry.arguments?.getString("difficulty") ?: "Kolay"
            val difficulty = Difficulty.valueOf(difficultyString)
            GameScreen(username = username, difficulty = difficulty, onNavigateHome = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            })
        }
        composable(Routes.SCORES) {
            ScoresScreen()
        }
        composable(Routes.SETTINGS) {
            SettingsScreen()
        }
    }
}
