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
    val id: Int,
    val id_dokter: Int,
    val id_pasien: Int,
    val tanggal_janji: String,
    val jam_janji: String,
    val keluhan: String,
    val status: StatusJanji,
    val nama_pasien: String? = "-"
)
