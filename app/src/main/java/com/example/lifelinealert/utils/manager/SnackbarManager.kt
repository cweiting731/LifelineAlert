package com.example.lifelinealert.utils.manager

import androidx.annotation.IntDef
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object SnackbarManager {

    val snackbarHostState = SnackbarHostState()

    fun showMessage(
        message: String,
        actionLabel: String ?= null,
        action: (() -> Unit) ?= null,
        nextAction: (() -> Unit) ?= null
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            // 顯示Snackbar並執行action
            showBar(message, actionLabel, action)
            // 等待 Snackbar 完成後執行下一個操作
            nextAction?.invoke()
        }
    }

    suspend fun showBar(
        message: String,
        actionLabel: String? = null,
        action: (() -> Unit)? = null
    ) {
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Indefinite
        )
        when (result) {
            SnackbarResult.ActionPerformed -> {
                action?.invoke()
            }
            SnackbarResult.Dismissed -> {
                /* Handle snackbar dismissed */
            }
        }
    }

}