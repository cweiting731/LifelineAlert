package com.example.lifelinealert.page.profileViewModel

import com.example.lifelinealert.utils.foreground.NotificationManager

data class ProfileUiState(
    val notificationManager: NotificationManager = NotificationManager(),
)
