package com.example.lifelinealert.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_profile")

data class UserProfile(private val context: Context) {
    companion object {
        private val IMAGE_URI_KEY = stringPreferencesKey("image_uri")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_PHONE_NUMBER = stringPreferencesKey("user_phone_number")
        private val USER_EMAIL = stringPreferencesKey("user_email")
    }

    suspend fun saveImageUri(uri: String) {
        context.dataStore.edit { profile ->
            profile[IMAGE_URI_KEY] = uri
        }
    }

    suspend fun saveUserProfile(name: String, phoneNumber: String, email: String) {
        context.dataStore.edit { profile ->
            profile[USER_NAME] = name
            profile[USER_PHONE_NUMBER] = phoneNumber
            profile[USER_EMAIL] = email
        }
    }

    val imageUriFlow: Flow<String?> = context.dataStore.data.map { profile ->
        profile[IMAGE_URI_KEY]
    }
}
