package com.example.say.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.say.R
import com.example.say.ui.theme.SayıTheme

enum class Difficulty {
    Kolay,
    Zor
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSetupScreen(modifier: Modifier = Modifier, onStartGame: (String, Difficulty) -> Unit) {
    var username by remember { mutableStateOf("") }
    val difficulties = Difficulty.values()
    var selectedDifficulty by remember { mutableStateOf(Difficulty.Kolay) }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.game_setup_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(id = R.string.username_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(id = R.string.difficulty_title), style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                difficulties.forEach { difficulty ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (difficulty == selectedDifficulty),
                                onClick = { selectedDifficulty = difficulty }
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = (difficulty == selectedDifficulty),
                            onClick = { selectedDifficulty = difficulty }
                        )
                        Text(
                            text = when (difficulty) {
                                Difficulty.Kolay -> stringResource(id = R.string.difficulty_easy)
                                Difficulty.Zor -> stringResource(id = R.string.difficulty_hard)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { onStartGame(username, selectedDifficulty) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = username.isNotBlank()
            ) {
                Text(
                    text = stringResource(id = R.string.start_game_button),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameSetupScreenPreview() {
    SayıTheme(darkTheme = false) {
        GameSetupScreen(onStartGame = { _, _ -> })
    }
}
