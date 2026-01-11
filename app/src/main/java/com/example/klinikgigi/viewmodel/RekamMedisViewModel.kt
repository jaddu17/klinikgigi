package com.example.klinikgigi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klinikgigi.modeldata.RekamMedis
import com.example.klinikgigi.modeldata.Tindakan
import com.example.klinikgigi.modeldata.JanjiTemu
import com.example.klinikgigi.repository.RepositoryKlinik
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RekamMedisViewModel(
    private val repository: RepositoryKlinik
) : ViewModel() {

    // =======================
    // DATA STATE
    // =======================

    private val _rekamMedisList = MutableStateFlow<List<RekamMedis>>(emptyList())
    val rekamMedisList: StateFlow<List<RekamMedis>> = _rekamMedisList

    private val _selectedRekamMedis = MutableStateFlow<RekamMedis?>(null)
    val selectedRekamMedis: StateFlow<RekamMedis?> = _selectedRekamMedis

    private val _tindakanList = MutableStateFlow<List<Tindakan>>(emptyList())
    val tindakanList: StateFlow<List<Tindakan>> = _tindakanList

    private val _janjiList = MutableStateFlow<List<JanjiTemu>>(emptyList())
    val janjiList: StateFlow<List<JanjiTemu>> = _janjiList

    // =======================
    // UI STATE
    // =======================

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    // =======================
    // LOAD DATA
    // =======================

    // alias list
    val rekamMedis: StateFlow<List<RekamMedis>> = rekamMedisList

    // alias loading
    val isLoading: StateFlow<Boolean> = loading

    // alias error
    val errorMessage: StateFlow<String?> = status

    // alias function
    fun getAllRekamMedis() {
        loadRekamMedis()
    }
    fun loadRekamMedis() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _rekamMedisList.value = repository.getRekamMedis()
            } catch (e: Exception) {
                _status.value = "Gagal memuat rekam medis"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadRekamMedisById(idRekam: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val data = repository.getRekamMedis()
                _selectedRekamMedis.value =
                    data.firstOrNull { it.id_rekam == idRekam }
            } catch (e: Exception) {
                _status.value = "Data tidak ditemukan"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadTindakan() {
        viewModelScope.launch {
            try {
                _tindakanList.value = repository.getTindakan()
            } catch (e: Exception) {
                _status.value = "Gagal memuat tindakan"
            }
        }
    }

    fun loadJanjiTemu() {
        viewModelScope.launch {
            try {
                _janjiList.value = repository.getJanjiTemu()
            } catch (e: Exception) {
                _status.value = "Gagal memuat janji temu"
            }
        }
    }

    // =======================
    // CRUD
    // =======================

    fun createRekamMedis(data: RekamMedis) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.createRekamMedis(data)
                if (response.isSuccessful) {
                    loadRekamMedis()
                    _success.value = true
                } else {
                    _status.value = "Gagal menambahkan rekam medis"
                }
            } catch (e: Exception) {
                _status.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateRekamMedis(data: RekamMedis) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.updateRekamMedis(data)
                if (response.isSuccessful) {
                    loadRekamMedis()
                    _success.value = true
                } else {
                    _status.value = "Gagal memperbarui rekam medis"
                }
            } catch (e: Exception) {
                _status.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteRekamMedis(idRekam: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.deleteRekamMedis(idRekam)
                if (response.isSuccessful) {
                    loadRekamMedis()
                } else {
                    _status.value = "Gagal menghapus rekam medis"
                }
            } catch (e: Exception) {
                _status.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // =======================
    // UTIL
    // =======================

    fun clearStatus() {
        _status.value = null
    }

    fun clearSelected() {
        _selectedRekamMedis.value = null
    }

    fun clearSuccess() {
        _success.value = false
    }
}
