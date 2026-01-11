package com.example.klinikgigi.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class JanjiTemuPerDokter(
    val id_dokter: Int,
    val nama_dokter: String,
    val janji_temu: List<JanjiTemu> = emptyList()
)
