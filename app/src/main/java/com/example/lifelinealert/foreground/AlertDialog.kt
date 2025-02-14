package com.example.lifelinealert.foreground

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun AlertDialog(
    showDialog: MutableState<Boolean>,
    title: MutableState<String>,
    message: MutableState<String>
) {
    if (showDialog.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = title.value) },
            text = { Text(text = message.value) },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text(text = "確定")
                }
            })
    }
}