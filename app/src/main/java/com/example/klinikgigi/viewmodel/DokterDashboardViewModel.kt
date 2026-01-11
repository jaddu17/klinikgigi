package com.example.klinikgigi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klinikgigi.modeldata.JanjiTemuPerDokter
import com.example.klinikgigi.repository.RepositoryKlinik
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DokterDashboardViewModel(
    private val repository: RepositoryKlinik
) : ViewModel() {

    private val _jadwalKlinik =
        MutableStateFlow<List<JanjiTemuPerDokter>>(emptyList())
    val jadwalKlinik: StateFlow<List<JanjiTemuPerDokter>> = _jadwalKlinik

    val isLoading = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)

    fun loadJadwalKlinik() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                _jadwalKlinik.value = repository.getJanjiTemuPerDokter()
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }
}
