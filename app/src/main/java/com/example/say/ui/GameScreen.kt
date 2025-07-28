package com.example.say.ui

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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
            Text(text = "Skor: ${gameState.score}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Süre: ${gameState.timeLeft}s", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
        onDismissRequest = onNavigateHome, // Arka plana tıklayınca anasayfaya döner
        title = {
            Text(
                text = if (gameResult == GameResult.WON) "Tebrikler, Kazandınız!" else "Süre Doldu, Kaybettiniz!",
                fontWeight = FontWeight.Bold
            )
        },
        text = { Text(text = "Skorunuz: $score") },
        confirmButton = {
            Column(horizontalAlignment = Alignment.End) {
                Button(onClick = onPlayAgain) {
                    Text("Tekrar Oyna")
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (gameResult == GameResult.WON) {
                    Button(onClick = onSaveScore) {
                        Text("Skoru Kaydet ve Çık")
                    }
                }
            }
        },
        dismissButton = {
            Button(onClick = onNavigateHome) {
                Text("Çıkış")
            }
        }
    )
}

@Composable
fun CardView(card: CardData, onClick: () -> Unit) {
    val rotation by animateFloatAsState(targetValue = if (card.isFlipped) 180f else 0f, label = "")

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
            .clickable(enabled = !card.isMatched && !card.isFlipped) { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (card.isMatched) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primary)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (rotation > 90f) {
                Text(
                    text = card.value.toString(),
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.graphicsLayer { rotationY = 180f } // Text is also rotated
                )
            }
        }
    }
}

@Preview(name = "Game Screen Light", showBackground = true)
@Composable
fun GameScreenPreviewLight() {
    SayıTheme(darkTheme = false) {
        GameScreen(username = "Tester", difficulty = Difficulty.Kolay, onNavigateHome = {})
    }
}

@Preview(name = "Game Screen Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameScreenPreviewDark() {
    SayıTheme(darkTheme = true) {
        GameScreen(username = "Tester", difficulty = Difficulty.Kolay, onNavigateHome = {})
    }
}
