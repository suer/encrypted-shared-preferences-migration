package com.example.encryptedsharedpreferencesmigration

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.encryptedsharedpreferencesmigration.ui.theme.EncryptedSharedPreferencesMigrationTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preference = Preference(this)
        enableEdgeToEdge()
        setContent {
            EncryptedSharedPreferencesMigrationTheme {
                val scope = rememberCoroutineScope()
                var text by remember { mutableStateOf("") }
                LaunchedEffect(Unit) {
                    text = preference.textFlow.first()
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("テキスト") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Button(
                            onClick = { scope.launch { preference.save(text) } },
                            modifier = Modifier.padding(top = 16.dp),
                        ) {
                            Text("保存")
                        }
                    }
                }
            }
        }
    }
}
