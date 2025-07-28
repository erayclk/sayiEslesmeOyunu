package com.example.say.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.say.MainViewModel
import com.example.say.MainViewModelFactory
import com.example.say.data.Theme
import androidx.compose.ui.tooling.preview.Preview
import com.example.say.ui.theme.SayıTheme

@Composable
fun SettingsScreen(mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(LocalContext.current.applicationContext as android.app.Application))) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val currentTheme by mainViewModel.theme.collectAsState()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Skorları Sil", fontWeight = FontWeight.Bold) },
            text = { Text("Tüm skorları silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.") },
            confirmButton = {
                Button(
                    onClick = { 
                        mainViewModel.deleteAllScores()
                        Toast.makeText(context, "Tüm skorlar silindi.", Toast.LENGTH_SHORT).show()
                        showDialog = false 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Hayır")
                }
            }
        )
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
        Text("Ayarlar", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Clear Scores Section
        Text("Veri Yönetimi", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Text("Tüm Skorları Sil", color = MaterialTheme.colorScheme.onErrorContainer)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Theme Selection Section
        Text("Tema", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Theme.values().forEach { theme ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { mainViewModel.setTheme(theme) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (currentTheme == theme),
                        onClick = { mainViewModel.setTheme(theme) }
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = when (theme) {
                            Theme.LIGHT -> "Açık"
                            Theme.DARK -> "Koyu"
                            Theme.SYSTEM -> "Sistem Varsayılanı"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } // Theme selection column


        } // Main Column
    } // Scaffold
}

@Preview(name = "Settings Screen Light", showBackground = true)
@Composable
fun SettingsScreenPreviewLight() {
    SayıTheme(darkTheme = false) {
        SettingsScreen()
    }
}

@Preview(name = "Settings Screen Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenPreviewDark() {
    SayıTheme(darkTheme = true) {
        SettingsScreen()
    }
}
