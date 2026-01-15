package com.example.klinikgigi.uicontroller.route

import com.example.klinikgigi.R

object DestinasiRekamMedisByJanji : DestinasiNavigasi {

    override val route = "rekam_medis_by_janji"
    override val titleRes = R.string.rekam_medis

    const val ID_JANJI = "idJanji"

    fun createRoute(idJanji: Int): String {
        return "$route/$idJanji"
    }
}
