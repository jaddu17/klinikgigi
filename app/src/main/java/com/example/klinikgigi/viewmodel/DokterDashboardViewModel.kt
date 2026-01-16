package com.example.klinikgigi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klinikgigi.modeldata.JanjiTemuPerDokter
import com.example.klinikgigi.repository.RepositoryKlinik
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DokterDashboardViewModel(
    private val repository: RepositoryKlinik
) : ViewModel() {

    private val _jadwalKlinik = MutableStateFlow<List<JanjiTemuPerDokter>>(emptyList())
    val jadwalKlinik: StateFlow<List<JanjiTemuPerDokter>> = _jadwalKlinik

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val filteredJadwalKlinik: StateFlow<List<JanjiTemuPerDokter>> = combine(
        _jadwalKlinik,
        _searchQuery
    ) { jadwal, query ->
        if (query.isBlank()) {
            jadwal
        } else {
            jadwal.filter { it.nama_dokter.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _selectedDokter = MutableStateFlow<JanjiTemuPerDokter?>(null)
    val selectedDokter: StateFlow<JanjiTemuPerDokter?> = _selectedDokter

    val isLoading = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)

    fun setSelectedDokter(dokter: JanjiTemuPerDokter) {
        _selectedDokter.value = dokter
    }

    fun clearSelectedDokter() {
        _selectedDokter.value = null
    }

    fun loadJadwalKlinik() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                _jadwalKlinik.value = repository.getJanjiTemuPerDokter()
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Gagal memuat data"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun loadJanjiTemuByDokterId(dokterId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val allData = repository.getJanjiTemuPerDokter()
                val found = allData.find { it.id_dokter == dokterId }
                if (found != null) {
                    _selectedDokter.value = found
                } else {
                    errorMessage.value = "Dokter tidak ditemukan"
                }
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Gagal memuat data"
            } finally {
                isLoading.value = false
            }
        }
    }
}