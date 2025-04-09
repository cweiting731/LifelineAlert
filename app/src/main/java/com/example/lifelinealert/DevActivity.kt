package com.example.lifelinealert

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lifelinealert.data.PublicDataHolder

class DevActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("DevActivity", "onCreate")
        enableEdgeToEdge()
        setContent {
            DevPage()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.v("DevActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("DevActivity", "onDestroy")
    }

    @Composable
    fun DevPage() {
        var inputIp by remember { mutableStateOf("") }
        var currentIP by remember { mutableStateOf(PublicDataHolder.serverIP) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "開發者模式 - 設定允許的 IP")
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = inputIp,
                onValueChange = { inputIp = it },
                label = { Text("請輸入允許的 IP") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                PublicDataHolder.serverIP = "ws://" + inputIp
                currentIP = PublicDataHolder.serverIP
                Log.v("DevActivity", "save inputIP ${PublicDataHolder.serverIP}")
            }) {
                Text("保存")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "當前保存的 IP: ${currentIP}")
        }
    }
}