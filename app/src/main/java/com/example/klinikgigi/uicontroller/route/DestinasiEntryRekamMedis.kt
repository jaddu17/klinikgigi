package com.example.klinikgigi.uicontroller.route

import com.example.klinikgigi.R

object DestinasiEntryRekamMedis : DestinasiNavigasi {
    override val route = "entry_rekam_medis/{idJanji}"
    override val titleRes = R.string.entry_rekam_medis

    fun createRoute(idJanji: Int): String {
        return "entry_rekam_medis/$idJanji"
    }
}
