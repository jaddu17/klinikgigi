package com.example.klinikgigi.remote

import com.example.klinikgigi.modeldata.Dokter
import com.example.klinikgigi.modeldata.JanjiTemu
import com.example.klinikgigi.modeldata.JanjiTemuPerDokter
import com.example.klinikgigi.modeldata.Pasien
import com.example.klinikgigi.modeldata.RekamMedis
import com.example.klinikgigi.modeldata.Tindakan
import com.example.klinikgigi.modeldata.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ServiceApiKlinik {
    @POST("users/login.php")
    suspend fun login(@Body body: Map<String, String>): User

    @POST("users/register.php")
    suspend fun register(@Body body: Map<String, String>): Response<User>

    // ---------------- DOKTER ----------------
    @GET("dokter/read.php")
    suspend fun getDokter(@Query("search") search: String? = null): List<Dokter>

    @POST("dokter/create.php")
    suspend fun createDokter(@Body dokter: Dokter): Response<Void>

    @PUT("dokter/update.php")
    suspend fun updateDokter(@Body dokter: Dokter): Response<Void>

    @DELETE("dokter/delete.php")
    suspend fun deleteDokter(@Query("id") id: Int): Response<Void>

    // ---------------- PASIEN ----------------
    @GET("pasien/read.php")
    suspend fun getPasien(@Query("search") search: String? = null): List<Pasien>

    @POST("pasien/create.php")
    suspend fun createPasien(@Body pasien: Pasien): Response<Void>

    @PUT("pasien/update.php")
    suspend fun updatePasien(@Body pasien: Pasien): Response<Void>

    @DELETE("pasien/delete.php")
    suspend fun deletePasien(@Query("id") id: Int): Response<Void>


    // ---------------- JANJI TEMU ----------------
    @GET("janji/read.php")
    suspend fun getJanjiTemu(@Query("search") search: String? = null): List<JanjiTemu>

    @POST("janji/create.php")
    suspend fun createJanjiTemu(@Body janji: JanjiTemu): Response<Void>

    @PUT("janji/update.php")
    suspend fun updateJanjiTemu(@Body janji: JanjiTemu): Response<Void>

    @DELETE("janji/delete.php")
    suspend fun deleteJanjiTemu(@Query("id") id: Int): Response<Void>

    @GET("janji/read_grouped.php")
    suspend fun getJanjiTemuPerDokter(): List<JanjiTemuPerDokter>

    // ---------------- TINDAKAN ----------------
    @GET("tindakan/read.php")
    suspend fun getTindakan(@Query("search") search: String? = null): List<Tindakan>

    @POST("tindakan/create.php")
    suspend fun createTindakan(@Body tindakan: Tindakan): Response<Void>

    @PUT("tindakan/update.php")
    suspend fun updateTindakan(@Body tindakan: Tindakan): Response<Void>

    @DELETE("tindakan/delete.php")
    suspend fun deleteTindakan(@Query("id") id: Int): Response<Void>

    // ================= REKAM MEDIS =================
    @GET("rekam_medis/read.php")
    suspend fun getRekamMedis(): List<RekamMedis>

    @POST("rekam_medis/create.php")
    suspend fun createRekamMedis(@Body rekamMedis: RekamMedis): Response<Void>

    @PUT("rekam_medis/update.php")
    suspend fun updateRekamMedis(@Body rekamMedis: RekamMedis): Response<Void>

    @DELETE("rekam_medis/delete.php")
    suspend fun deleteRekamMedis(@Query("id") id: Int): Response<Void>
}
