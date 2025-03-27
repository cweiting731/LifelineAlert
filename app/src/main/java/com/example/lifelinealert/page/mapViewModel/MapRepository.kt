package com.example.lifelinealert.page.mapViewModel

import com.google.firebase.Firebase
import com.google.firebase.database.database

class MapRepository {
    private val database = Firebase.database.getReference("users")
    private val userName = "Willy"

    fun getUserLastLocation() {

    }

}