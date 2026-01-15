package com.example.klinikgigi.modeldata

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
@Serializable
enum class StatusJanji {
    @SerialName("menunggu")
    MENUNGGU,

    @SerialName("selesai")
    SELESAI,

    @SerialName("batal")
    BATAL
}
@Serializable
data class JanjiTemu(
    @SerialName("id")
    val id_janji: Int,
    val id_dokter: Int,
    val id_pasien: Int,
    val tanggal_janji: String,
    val jam_janji: String,
    val keluhan: String,
    val status: StatusJanji,
    val nama_pasien: String? = "-",

    val sudah_ada_rekam_medis: Boolean = false
)
