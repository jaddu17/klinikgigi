package com.example.klinikgigi.repository

import com.example.klinikgigi.modeldata.User
import com.example.klinikgigi.remote.ServiceApiKlinik
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.Response

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

    suspend fun checkUsernameExists(username: String): Boolean {
        return try {
            val response = api.checkUsername(mapOf("username" to username))
            response["exists"] as? Boolean ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun register(username: String, password: String, role: String): User {
        val response: Response<User> = api.register(
            mapOf("username" to username, "password" to password, "role" to role)
        )

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Data kosong")
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMsg = try {
                val json = Json.parseToJsonElement(errorBody ?: "{}")
                json.jsonObject["error"]?.jsonPrimitive?.content ?: "Error tidak diketahui"
            } catch (e: Exception) {
                "Error jaringan"
            }
            throw Exception(errorMsg)
        }
    }
}
