package com.example.klinikgigi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klinikgigi.modeldata.User
import com.example.klinikgigi.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // ================= LOGIN =================
    fun login(username: String, password: String) {

        if (username.isBlank() || password.isBlank()) {
            _message.value = "Username dan password wajib diisi"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                val user = repository.login(username, password)

                _user.value = user
                _message.value = "success"

            } catch (e: Exception) {
                _user.value = null
                _message.value = "Login gagal"
            } finally {
                _loading.value = false
            }
        }
    }

    // ================= REGISTER =================
    fun register(username: String, password: String, role: String) {

        viewModelScope.launch {
            try {
                _loading.value = true
                repository.register(username, password, role)
                _message.value = "Registrasi berhasil"
            } catch (e: Exception) {
                _message.value = "Registrasi gagal"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
