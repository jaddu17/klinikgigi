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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _janjiSudahAdaRekamMedis =
        MutableStateFlow<Set<Int>>(emptySet())
    val janjiSudahAdaRekamMedis: StateFlow<Set<Int>> =
        _janjiSudahAdaRekamMedis

    init {
        loadPasien()
        loadDokter()
        loadJanji()
        loadJanjiYangSudahAdaRekamMedis()
    }

    fun loadJanji() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _janjiList.value = repository.getJanjiTemu()
            } catch (e: Exception) {
                _status.value = "Gagal memuat janji"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun loadPasien() {
        viewModelScope.launch {
            _pasienList.value = repository.getPasien()
        }
    }

    private fun loadDokter() {
        viewModelScope.launch {
            _dokterList.value = repository.getDokter()
        }
    }

    fun loadJanjiYangSudahAdaRekamMedis() {
        viewModelScope.launch {
            try {
                _janjiSudahAdaRekamMedis.value =
                    repository.getRekamMedis()
                        .map { it.id_janji }
                        .toSet()
            } catch (e: Exception) {
                _status.value = "Gagal memuat rekam medis"
            }
        }
    }

    // ======================
    // CREATE (BENAR, TANPA success)
    // ======================
    // ======================
    // CREATE (BENAR, TANPA success)
    // ======================
    fun createJanjiTemu(janji: JanjiTemu) {
        viewModelScope.launch {
            try {
                if (validateConflict(janji)) return@launch

                val response = repository.createJanjiTemu(janji)

                if (!response.isSuccessful) {
                    _status.value = response.errorBody()?.string()
                        ?: "Gagal menambah janji"
                    return@launch
                }

                loadJanji()
                _status.value = "Berhasil menambah janji"

            } catch (e: Exception) {
                _status.value = "Gagal menambah janji"
            }
        }
    }

    // ======================
    // UPDATE
    // ======================
    // ======================
    // UPDATE
    // ======================
    fun updateJanjiTemu(janji: JanjiTemu) {
        viewModelScope.launch {
            try {
                if (validateConflict(janji)) return@launch

                val response = repository.updateJanjiTemu(janji)

                if (!response.isSuccessful) {
                    _status.value = response.errorBody()?.string()
                        ?: "Gagal update janji"
                    return@launch
                }

                loadJanji()
                _status.value = "Berhasil update janji"

            } catch (e: Exception) {
                _status.value = "Gagal update janji"
            }
        }
    }

    // ======================
    // DELETE
    // ======================
    fun deleteJanji(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteJanjiTemu(id)

                if (!response.isSuccessful) {
                    _status.value = response.errorBody()?.string()
                        ?: "Gagal hapus janji"
                    return@launch
                }

                loadJanji()
                loadJanjiYangSudahAdaRekamMedis()
                _status.value = "Berhasil menghapus janji"

            } catch (e: Exception) {
                _status.value = "Gagal hapus janji"
            }
        }
    }

    // ======================
// SEARCH JANJI
// ======================
    fun searchJanji(query: String) {
        _searchQuery.value = query

        viewModelScope.launch {
            _loading.value = true
            try {
                _janjiList.value =
                    if (query.isBlank()) {
                        repository.getJanjiTemu()
                    } else {
                        repository.getJanjiTemu(query)
                    }
            } catch (e: Exception) {
                _status.value = "Gagal mencari janji"
            } finally {
                _loading.value = false
            }
        }
    }

    // ======================
// LOAD JANJI BY ID
// ======================
    fun loadJanjiById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _selectedJanji.value =
                    repository.getJanjiTemu()
                        .firstOrNull { it.id_janji == id }
            } catch (e: Exception) {
                _status.value = "Gagal memuat janji"
            } finally {
                _loading.value = false
            }
        }
    }


    fun clearStatus() {
        _status.value = null
    }

    private fun validateConflict(newJanji: JanjiTemu): Boolean {
        val existingList = _janjiList.value

        // Helper untuk normalisasi waktu (ambil 5 karakter pertama: HH:mm)
        fun String.normalizeTime(): String = this.take(5)

        // Cek Duplikat Persis (Dokter sama & Pasien sama & Waktu sama)
        val exactDuplicate = existingList.find {
            it.id_dokter == newJanji.id_dokter &&
                    it.id_pasien == newJanji.id_pasien &&
                    it.tanggal_janji == newJanji.tanggal_janji &&
                    it.jam_janji.normalizeTime() == newJanji.jam_janji.normalizeTime() &&
                    it.id_janji != newJanji.id_janji
        }

        if (exactDuplicate != null) {
            _status.value = "Janji temu sudah ada"
            return true
        }

        // Cek konflik Dokter (Dokter yg sama tapi Pasien beda)
        val dokterConflict = existingList.find {
            it.id_dokter == newJanji.id_dokter &&
                    it.tanggal_janji == newJanji.tanggal_janji &&
                    it.jam_janji.normalizeTime() == newJanji.jam_janji.normalizeTime() &&
                    it.id_janji != newJanji.id_janji
        }

        if (dokterConflict != null) {
            _status.value = "Dokter ini sudah memiliki janji pada waktu tersebut"
            return true
        }

        // Cek konflik Pasien (Pasien yg sama tapi Dokter beda)
        val pasienConflict = existingList.find {
            it.id_pasien == newJanji.id_pasien &&
                    it.tanggal_janji == newJanji.tanggal_janji &&
                    it.jam_janji.normalizeTime() == newJanji.jam_janji.normalizeTime() &&
                    it.id_janji != newJanji.id_janji
        }

        if (pasienConflict != null) {
            _status.value = "Pasien ini sudah memiliki janji pada waktu tersebut"
            return true
        }

        return false
    }
}
