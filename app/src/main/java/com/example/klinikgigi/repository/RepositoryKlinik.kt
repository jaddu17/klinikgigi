package com.example.klinikgigi.repository

import com.example.klinikgigi.modeldata.Dokter
import com.example.klinikgigi.modeldata.JanjiTemu
import com.example.klinikgigi.modeldata.JanjiTemuPerDokter
import com.example.klinikgigi.modeldata.Pasien
import com.example.klinikgigi.modeldata.RekamMedis
import com.example.klinikgigi.modeldata.Tindakan
import com.example.klinikgigi.remote.ServiceApiKlinik
import retrofit2.Response

/**
 * Repository interface
 */
interface RepositoryKlinik {

    // ---------------- Dokter ----------------
    suspend fun getDokter(): List<Dokter>
    suspend fun getDokter(searchQuery: String? = null): List<Dokter>
    suspend fun createDokter(dokter: Dokter): Response<Void>
    suspend fun updateDokter(dokter: Dokter): Response<Void>
    suspend fun deleteDokter(id: Int): Response<Void>

    // ---------------- Pasien ----------------
    suspend fun getPasien(): List<Pasien>
    suspend fun getPasien(searchQuery: String? = null): List<Pasien>
    suspend fun createPasien(pasien: Pasien): Response<Void>
    suspend fun updatePasien(pasien: Pasien): Response<Void>
    suspend fun deletePasien(id: Int): Response<Void>

    // ---------------- Janji Temu ----------------
    suspend fun getJanjiTemu(): List<JanjiTemu>
    suspend fun createJanjiTemu(janji: JanjiTemu): Response<Void>
    suspend fun updateJanjiTemu(janji: JanjiTemu): Response<Void>
    suspend fun deleteJanjiTemu(id: Int): Response<Void>
    suspend fun getJanjiTemu(searchQuery: String? = null): List<JanjiTemu>

    // ---------------- Janji Temu (Grouped) ----------------
    suspend fun getJanjiTemuPerDokter(): List<JanjiTemuPerDokter>

    // ---------------- Tindakan ----------------
    suspend fun getTindakan(): List<Tindakan>
    suspend fun getTindakan(searchQuery: String? = null): List<Tindakan>
    suspend fun createTindakan(tindakan: Tindakan): Response<Void>
    suspend fun updateTindakan(tindakan: Tindakan): Response<Void>
    suspend fun deleteTindakan(id: Int): Response<Void>

    // ---------------- Rekam Medis ----------------
    suspend fun getRekamMedis(): List<RekamMedis>
    suspend fun createRekamMedis(rekamMedis: RekamMedis): Response<Void>
    suspend fun updateRekamMedis(rekamMedis: RekamMedis): Response<Void>
    suspend fun deleteRekamMedis(id: Int): Response<Void>
}

/**
 * Implementasi Repository
 */
class JaringanRepositoryKlinik(
    private val api: ServiceApiKlinik
) : RepositoryKlinik {

    // ---------------- Dokter ----------------
    override suspend fun getDokter(): List<Dokter> =
        api.getDokter()

    override suspend fun getDokter(searchQuery: String?): List<Dokter> =
        api.getDokter(searchQuery)

    override suspend fun createDokter(dokter: Dokter): Response<Void> =
        api.createDokter(dokter)

    override suspend fun updateDokter(dokter: Dokter): Response<Void> =
        api.updateDokter(dokter)

    override suspend fun deleteDokter(id: Int): Response<Void> =
        api.deleteDokter(id)

    // ---------------- Pasien ----------------
    override suspend fun getPasien(): List<Pasien> =
        api.getPasien()

    override suspend fun getPasien(searchQuery: String?): List<Pasien> =
        api.getPasien(searchQuery)

    override suspend fun createPasien(pasien: Pasien): Response<Void> =
        api.createPasien(pasien)

    override suspend fun updatePasien(pasien: Pasien): Response<Void> =
        api.updatePasien(pasien)

    override suspend fun deletePasien(id: Int): Response<Void> =
        api.deletePasien(id)

    // ---------------- Janji Temu ----------------
    override suspend fun getJanjiTemu(): List<JanjiTemu> =
        api.getJanjiTemu()

    override suspend fun createJanjiTemu(janji: JanjiTemu): Response<Void> =
        api.createJanjiTemu(janji)

    override suspend fun updateJanjiTemu(janji: JanjiTemu): Response<Void> =
        api.updateJanjiTemu(janji)

    override suspend fun deleteJanjiTemu(id: Int): Response<Void> =
        api.deleteJanjiTemu(id)

    override suspend fun getJanjiTemuPerDokter(): List<JanjiTemuPerDokter> =
        api.getJanjiTemuPerDokter()

    override suspend fun getJanjiTemu(searchQuery: String?): List<JanjiTemu> =
        api.getJanjiTemu(searchQuery)

    // ---------------- Tindakan ----------------
    override suspend fun getTindakan(): List<Tindakan> =
        api.getTindakan()

    override suspend fun getTindakan(searchQuery: String?): List<Tindakan> =
        api.getTindakan(searchQuery)

    override suspend fun createTindakan(tindakan: Tindakan): Response<Void> =
        api.createTindakan(tindakan)

    override suspend fun updateTindakan(tindakan: Tindakan): Response<Void> =
        api.updateTindakan(tindakan)

    override suspend fun deleteTindakan(id: Int): Response<Void> =
        api.deleteTindakan(id)


    // ---------------- Rekam Medis ----------------
    override suspend fun getRekamMedis(): List<RekamMedis> =
        api.getRekamMedis()

    override suspend fun createRekamMedis(rekamMedis: RekamMedis): Response<Void> =
        api.createRekamMedis(rekamMedis)

    override suspend fun updateRekamMedis(rekamMedis: RekamMedis): Response<Void> =
        api.updateRekamMedis(rekamMedis)

    override suspend fun deleteRekamMedis(id: Int): Response<Void> =
        api.deleteRekamMedis(id)
}
