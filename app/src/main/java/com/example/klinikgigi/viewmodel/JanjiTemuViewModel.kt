package com.example.klinikgigi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klinikgigi.modeldata.Dokter
import com.example.klinikgigi.modeldata.JanjiTemu
import com.example.klinikgigi.modeldata.Pasien
import com.example.klinikgigi.repository.RepositoryKlinik
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JanjiTemuViewModel(
    private val repository: RepositoryKlinik
) : ViewModel() {

    // ======================
    // STATE
    // ======================
    private val _janjiList = MutableStateFlow<List<JanjiTemu>>(emptyList())
    val janjiList: StateFlow<List<JanjiTemu>> = _janjiList

    private val _pasienList = MutableStateFlow<List<Pasien>>(emptyList())
    val pasienList: StateFlow<List<Pasien>> = _pasienList

    private val _dokterList = MutableStateFlow<List<Dokter>>(emptyList())
    val dokterList: StateFlow<List<Dokter>> = _dokterList

    private val _selectedJanji = MutableStateFlow<JanjiTemu?>(null)
    val selectedJanji: StateFlow<JanjiTemu?> = _selectedJanji

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    // ======================
    // INIT
    // ======================
    init {
        loadPasien()
        loadDokter()
        loadJanji()
    }

    // ======================
    // LOAD DATA
    // ======================
    fun loadJanji() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val dataDariServer = repository.getJanjiTemu()
                // .toList() sangat penting agar StateFlow mendeteksi referensi baru
                _janjiList.value = dataDariServer.toList()
            } catch (e: Exception) {
                _status.value = "Gagal memuat: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun loadPasien() {
        viewModelScope.launch {
            try {
                _pasienList.value = repository.getPasien().toList()
            } catch (e: Exception) {
                _status.value = "Gagal memuat pasien: ${e.message}"
            }
        }
    }

    private fun loadDokter() {
        viewModelScope.launch {
            try {
                _dokterList.value = repository.getDokter().toList()
            } catch (e: Exception) {
                _status.value = "Gagal memuat dokter: ${e.message}"
            }
        }
    }

    // ======================
    // LOAD PER ID (EDIT)
    // ======================
    fun loadJanjiById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val janji = repository.getJanjiTemu().find { it.id == id }
                _selectedJanji.value = janji
                if (janji == null) _status.value = "Janji tidak ditemukan"
            } catch (e: Exception) {
                _status.value = "Gagal memuat data: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // ======================
    // CREATE
    // ======================
    fun createJanjiTemu(janji: JanjiTemu) {
        viewModelScope.launch {
            _status.value = "loading"
            try {
                repository.createJanjiTemu(janji)
                // Refresh list dan buat list baru agar Compose recompose
                _janjiList.value = repository.getJanjiTemu().toList()
                _status.value = "Berhasil menambah janji"
            } catch (e: Exception) {
                _status.value = "Gagal menambah janji: ${e.message}"
            }
        }
    }

    // ======================
    // UPDATE
    // ======================
    fun updateJanjiTemu(janji: JanjiTemu) {
        viewModelScope.launch {
            _status.value = "loading"
            try {
                val response = repository.updateJanjiTemu(janji)
                if (response.isSuccessful) {
                    // PANGGIL loadJanji() yang sudah diperbaiki di atas
                    loadJanji()
                    _status.value = "Berhasil update janji"
                }
            } catch (e: Exception) {
                _status.value = "Gagal update: ${e.message}"
            }
        }
    }


    // ======================
    // DELETE
    // ======================
    fun deleteJanji(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteJanjiTemu(id)
                // Refresh list dan buat list baru agar Compose recompose
                _janjiList.value = repository.getJanjiTemu().toList()
                _status.value = "Berhasil menghapus janji"
            } catch (e: Exception) {
                _status.value = "Gagal hapus janji: ${e.message}"
            }
        }
    }

    // ======================
    // CLEAR STATUS
    // ======================
    fun clearStatus() {
        _status.value = null
    }
}
