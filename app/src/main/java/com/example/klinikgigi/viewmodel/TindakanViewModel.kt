package com.example.klinikgigi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klinikgigi.modeldata.Tindakan
import com.example.klinikgigi.repository.RepositoryKlinik
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TindakanViewModel(
    private val repo: RepositoryKlinik
) : ViewModel() {

    private val _tindakanList = MutableStateFlow<List<Tindakan>>(emptyList())
    val tindakanList: StateFlow<List<Tindakan>> = _tindakanList

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _selectedTindakan = MutableStateFlow<Tindakan?>(null)
    val selectedTindakan: StateFlow<Tindakan?> = _selectedTindakan

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadTindakan()
    }

    // ================= LOAD =================
    fun loadTindakan() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _tindakanList.value = repo.getTindakan()
            } finally {
                _loading.value = false
            }
        }
    }

    // ================= SEARCH =================
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        searchTindakan(query)
    }

    private fun searchTindakan(query: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _tindakanList.value =
                    if (query.isBlank()) {
                        repo.getTindakan()
                    } else {
                        repo.getTindakan(query)
                    }
            } finally {
                _loading.value = false
            }
        }
    }

    // ================= LOAD BY ID =================
    fun loadTindakanById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val list = repo.getTindakan()
                _selectedTindakan.value = list.find { it.id_tindakan == id }
            } finally {
                _loading.value = false
            }
        }
    }

    // ================= CRUD =================
    fun createTindakan(data: Tindakan) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repo.createTindakan(data)
                loadTindakan()
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateTindakan(data: Tindakan) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repo.updateTindakan(data)
                loadTindakan()
                _selectedTindakan.value = data
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteTindakan(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repo.deleteTindakan(id)
                loadTindakan()
            } finally {
                _loading.value = false
            }
        }
    }
}
