package com.example.klinikgigi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klinikgigi.modeldata.Pasien
import com.example.klinikgigi.repository.RepositoryKlinik
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class PasienViewModel(
    private val repository: RepositoryKlinik
) : ViewModel() {

    // ================= STATE =================
    private val _pasienList = MutableStateFlow<List<Pasien>>(emptyList())
    val pasienList: StateFlow<List<Pasien>> = _pasienList

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _selectedPasien = MutableStateFlow<Pasien?>(null)
    val selectedPasien: StateFlow<Pasien?> = _selectedPasien

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadPasien()
    }

    // ================= LOAD PASIEN =================
    fun loadPasien() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _pasienList.value = repository.getPasien()
            } catch (e: Exception) {
                _message.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // ================= SEARCH PASIEN =================
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        searchPasien(query)
    }

    private fun searchPasien(query: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _pasienList.value =
                    if (query.isBlank()) {
                        repository.getPasien()
                    } else {
                        repository.getPasien(query)
                    }
            } catch (e: Exception) {
                _message.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // ================= LOAD BY ID =================
    fun loadPasienById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val list = repository.getPasien()
                _selectedPasien.value = list.find { it.id_pasien == id }
            } finally {
                _loading.value = false
            }
        }
    }

    // ================= CREATE =================
    // ================= CREATE =================
    fun createPasien(pasien: Pasien) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.createPasien(pasien)
                if (response.isSuccessful) {
                    _message.value = "Pasien berhasil ditambahkan"
                    loadPasien()
                } else {
                    val errorBody = response.errorBody()?.string()
                    _message.value = parseError(errorBody)
                }
            } catch (e: Exception) {
                _message.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // ================= UPDATE =================
    fun updatePasien(pasien: Pasien) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.updatePasien(pasien)
                if (response.isSuccessful) {
                    _message.value = "Pasien berhasil diperbarui"
                    loadPasien()
                    _selectedPasien.value = pasien
                } else {
                    val errorBody = response.errorBody()?.string()
                    _message.value = parseError(errorBody)
                }
            } catch (e: Exception) {
                _message.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // ================= DELETE =================
    fun deletePasien(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.deletePasien(id)
                loadPasien()
            } finally {
                _loading.value = false
            }
        }
    }

    // ================= UTIL =================
    fun clearMessage() {
        _message.value = null
    }

    private fun parseError(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) return "Terjadi kesalahan"
        return try {
            val json = JSONObject(errorBody)
            json.optString("error", "Terjadi kesalahan")
        } catch (e: Exception) {
            "Terjadi kesalahan"
        }
    }
}
