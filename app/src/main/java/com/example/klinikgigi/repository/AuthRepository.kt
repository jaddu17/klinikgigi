package com.example.klinikgigi.repository

import com.example.klinikgigi.modeldata.User
import com.example.klinikgigi.remote.ServiceApiKlinik

class AuthRepository(
    private val api: ServiceApiKlinik
) {

    suspend fun login(username: String, password: String): User {
        return api.login(
            mapOf(
                "username" to username,
                "password" to password
            )
        )
    }

    suspend fun register(username: String, password: String, role: String): User {
        return api.register(
            mapOf(
                "username" to username,
                "password" to password,
                "role" to role
            )
        )
    }
}
