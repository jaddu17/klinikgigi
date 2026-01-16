package com.example.klinikgigi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klinikgigi.modeldata.Dokter
import com.example.klinikgigi.repository.RepositoryKlinik
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class DokterViewModel(private val repository: RepositoryKlinik) : ViewModel() {

    private val _dokterList = MutableStateFlow<List<Dokter>>(emptyList())
    val dokterList: StateFlow<List<Dokter>> = _dokterList

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedDokter = MutableStateFlow<Dokter?>(null)
    val selectedDokter: StateFlow<Dokter?> = _selectedDokter

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    init {
        loadDokter()
    }

    fun clearMessage() {
        _message.value = null
    }

    /** ======================= LOAD SEMUA DOKTER (dengan pencarian opsional) ======================= */
    fun loadDokter(searchQuery: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _dokterList.value = repository.getDokter(searchQuery)
            } catch (e: Exception) {
                _message.value = e.message ?: "Gagal memuat data dokter"
            } finally {
                _loading.value = false
            }
        }
    }

    /** ======================= SET QUERY PENCARIAN ======================= */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        // Kirim null jika query kosong atau hanya spasi, agar backend kembalikan semua data
        loadDokter(if (query.isNullOrBlank()) null else query)
    }

    /** ======================= CREATE DOKTER ======================= */
    fun createDokter(dokter: Dokter) {
        viewModelScope.launch {
            _loading.value = true
            try {
                // Cek apakah nama dokter sudah ada (case-insensitive)
                val isDuplicate = _dokterList.value.any { it.nama_dokter.equals(dokter.nama_dokter, ignoreCase = true) }
                if (isDuplicate) {
                    _message.value = "Nama dokter sudah ditambahkan"
                    // Return agar tidak lanjut ke API call
                    return@launch 
                }

                val response = repository.createDokter(dokter)
                if (response.isSuccessful) {
                    _message.value = "Dokter berhasil ditambahkan"
                    loadDokter(_searchQuery.value.ifBlank { null }) // refresh sesuai query saat ini
                } else {
                    _message.value = "Gagal menyimpan data dokter: ${response.code()}"
                }
            } catch (e: Exception) {
                _message.value = e.message ?: "Gagal menyimpan data dokter"
            } finally {
                _loading.value = false
            }
        }
    }

    /** ======================= UPDATE DOKTER ======================= */
    fun updateDokter(dokter: Dokter) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.updateDokter(dokter)
                _message.value = "Data dokter berhasil diperbarui"
                _selectedDokter.value = null // reset seleksi setelah update (opsional)
                loadDokter(_searchQuery.value.ifBlank { null })
            } catch (e: Exception) {
                _message.value = e.message ?: "Gagal memperbarui data dokter"
            } finally {
                _loading.value = false
            }
        }
    }

    /** ======================= HAPUS DOKTER ======================= */
    fun deleteDokter(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.deleteDokter(id)
                if (response.isSuccessful) {
                    _message.value = "Dokter berhasil dihapus"
                    loadDokter(_searchQuery.value.ifBlank { null })
                } else {
                    // Parse error message from backend
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = try {
                        // Try to parse JSON error response
                        val json = kotlinx.serialization.json.Json.parseToJsonElement(errorBody ?: "{}")
                        json.jsonObject["error"]?.jsonPrimitive?.content 
                            ?: "Gagal menghapus dokter"
                    } catch (e: Exception) {
                        "Gagal menghapus dokter"
                    }
                    _message.value = errorMsg
                }
            } catch (e: Exception) {
                _message.value = e.message ?: "Gagal menghapus dokter"
            } finally {
                _loading.value = false
            }
        }
    }

    /** ======================= PILIH / SET DOKTER UNTUK EDIT ======================= */
    fun selectDokter(dokter: Dokter?) {
        _selectedDokter.value = dokter
    }
}