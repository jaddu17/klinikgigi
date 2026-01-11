package com.example.klinikgigi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klinikgigi.modeldata.Dokter
import com.example.klinikgigi.repository.RepositoryKlinik
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DokterViewModel(private val repository: RepositoryKlinik) : ViewModel() {

    private val _dokterList = MutableStateFlow<List<Dokter>>(emptyList())
    val dokterList: StateFlow<List<Dokter>> get() = _dokterList

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _selectedDokter = MutableStateFlow<Dokter?>(null)

    init {
        loadDokter()
    }

    /** ======================= LOAD SEMUA DOKTER ======================= */
    fun loadDokter() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _dokterList.value = repository.getDokter()
            } finally {
                _loading.value = false
            }
        }
    }



    /** ======================= CREATE DOKTER ======================= */
    fun createDokter(dokter: Dokter) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.createDokter(dokter)
                loadDokter()   // refresh list
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
                loadDokter()            // refresh list
                _selectedDokter.value = dokter   // simpan kembali agar form tidak hilang
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
                repository.deleteDokter(id)
                loadDokter()
            } finally {
                _loading.value = false
            }
        }
    }
}
