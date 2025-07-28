package com.example.say.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.stringResource
import com.example.say.R
import com.example.say.data.Language
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.say.MainViewModel

import com.example.say.data.Theme
import androidx.compose.ui.tooling.preview.Preview
import com.example.say.ui.theme.SayıTheme

@Composable
fun SettingsScreen(mainViewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
        val currentTheme by mainViewModel.theme.collectAsState()
    val currentLanguage by mainViewModel.language.collectAsState()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
                        title = { Text(stringResource(id = R.string.delete_scores_dialog_title), fontWeight = FontWeight.Bold) },
                        text = { Text(stringResource(id = R.string.delete_scores_dialog_text)) },
            confirmButton = {
                Button(
                    onClick = { 
                        mainViewModel.deleteAllScores()
                                                Toast.makeText(context, context.getString(R.string.all_scores_deleted_toast), Toast.LENGTH_SHORT).show()
                        showDialog = false 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                                        Text(stringResource(id = R.string.delete_button))
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                                        Text(stringResource(id = R.string.no_button))
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
                Text(stringResource(id = R.string.settings_button), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Clear Scores Section
                Text(stringResource(id = R.string.data_management_title), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
                        Text(stringResource(id = R.string.delete_all_scores), color = MaterialTheme.colorScheme.onErrorContainer)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Theme Selection Section
                Text(stringResource(id = R.string.theme_title), style = MaterialTheme.typography.titleLarge)
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
                            Theme.LIGHT -> stringResource(id = R.string.theme_light)
                            Theme.DARK -> stringResource(id = R.string.theme_dark)
                            Theme.SYSTEM -> stringResource(id = R.string.theme_system)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
                } // Theme selection column

        Spacer(modifier = Modifier.height(32.dp))

        // Language Selection Section
        Text(stringResource(id = R.string.language_title), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Language.values().forEach { language ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            mainViewModel.setLanguage(language)
                            // Apply locale immediately so Settings screen updates without leaving
                            val locale = java.util.Locale(language.code)
                            val localeList = androidx.core.os.LocaleListCompat.forLanguageTags(locale.toLanguageTag())
                            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(localeList)
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (currentLanguage == language),
                        onClick = {
                            mainViewModel.setLanguage(language)
                            val locale = java.util.Locale(language.code)
                            val localeList = androidx.core.os.LocaleListCompat.forLanguageTags(locale.toLanguageTag())
                            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(localeList)
                        }
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = when (language) {
                            Language.TURKISH -> stringResource(id = R.string.language_turkish)
                            Language.ENGLISH -> stringResource(id = R.string.language_english)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } // Language selection column


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
