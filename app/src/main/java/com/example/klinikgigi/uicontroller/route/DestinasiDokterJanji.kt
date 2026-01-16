package com.example.klinikgigi.uicontroller.route

import com.example.klinikgigi.R

object DestinasiDokterJanji : DestinasiNavigasi {
    override val route = "dokter_janji_temu"
    override val titleRes = R.string.dokter_janji
    
    const val idDokterArg = "idDokter"
    val routeWithArgs = "$route/{$idDokterArg}"
    
    fun createRoute(idDokter: Int) = "$route/$idDokter"
}
