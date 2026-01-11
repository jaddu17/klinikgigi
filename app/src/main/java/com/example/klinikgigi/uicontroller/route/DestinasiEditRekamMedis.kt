package com.example.klinikgigi.uicontroller.route

import com.example.klinikgigi.R

object DestinasiEditRekamMedis : DestinasiNavigasi {
    override val route = "edit_rekam_medis/{idRekamMedis}"
    override val titleRes = R.string.edit_rekam_medis

    fun createRoute(id: Int) = "edit_rekam_medis/$id"
}
