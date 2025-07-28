package com.example.say.ui

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.say.R
import com.example.say.data.ScoreRepository
import androidx.compose.ui.tooling.preview.Preview
import com.example.say.ui.theme.SayıTheme

@Composable
fun GameScreen(username: String, difficulty: Difficulty, onNavigateHome: () -> Unit) {
    val context = LocalContext.current
    val scoreRepository = remember { ScoreRepository(context) }
    val viewModel: GameViewModel = viewModel(factory = GameViewModelFactory(username, difficulty, scoreRepository))
    val gameState by viewModel.gameState.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "${stringResource(id = R.string.score_label)}: ${gameState.score}", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stringResource(id = R.string.time_label)}: ${gameState.timeLeft}s", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (difficulty == Difficulty.Kolay) 4 else 4),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(gameState.cards) { card ->
                    CardView(card = card, onClick = { viewModel.onCardClicked(card) })
                }
            }

            if (gameState.gameResult != GameResult.IN_PROGRESS) {
                GameOverDialog(
                    gameResult = gameState.gameResult,
                    score = gameState.score,
                    onPlayAgain = { viewModel.startGame() },
                    onNavigateHome = onNavigateHome,
                    onSaveScore = {
                        viewModel.saveScore()
                        onNavigateHome()
                    }
                )
            }
        } // Column
    } // Scaffold
}

@Composable
fun GameOverDialog(
    gameResult: GameResult,
    score: Int,
    onPlayAgain: () -> Unit,
    onSaveScore: () -> Unit,
    onNavigateHome: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = { 
            Text(
                text = stringResource(id = R.string.game_over_title), 
                fontWeight = FontWeight.Bold
            ) 
        },
        text = { 
            Text(
                text = when (gameResult) {
                    GameResult.WON -> stringResource(id = R.string.you_won)
                    GameResult.LOST_TIME -> stringResource(id = R.string.you_lost)
                    GameResult.IN_PROGRESS -> ""
                }
            ) 
        },
        confirmButton = {
            Button(onClick = onPlayAgain) {
                Text(stringResource(id = R.string.play_again_button))
            }
        },
        dismissButton = {
            Button(onClick = onNavigateHome) {
                Text(stringResource(id = R.string.back_to_home_button))
            }
        }
    )
}

@Composable
fun CardView(card: CardData, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped) 180f else 0f,
        label = "card_rotation"
    )

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation
                },
            contentAlignment = Alignment.Center
        ) {
            if (card.isFlipped || card.isMatched) {
                Text(
                    text = card.value.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    SayıTheme(darkTheme = false) {
        GameScreen(username = "Test", difficulty = Difficulty.Kolay, onNavigateHome = {})
    }
}
