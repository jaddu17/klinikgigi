package com.example.klinikgigi.uicontroller.route

import com.example.klinikgigi.R

object DestinasiRekamMedisByJanji : DestinasiNavigasi {

    override val route = "rekam_medis_by_janji"
    override val titleRes = R.string.rekam_medis

    const val ID_JANJI = "idJanji"
    const val IS_ADMIN = "isAdmin"

    val routeWithArgs = "$route/{$ID_JANJI}/{$IS_ADMIN}"

    fun createRoute(idJanji: Int, isAdmin: Boolean): String {
        return "$route/$idJanji/$isAdmin"
    }
}
