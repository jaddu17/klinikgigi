package com.example.klinikgigi.uicontroller.route

object DestinasiEditDokter {
    const val route = "edit_dokter"
    const val dokterIdArg = "id_dokter"

    val routeWithArgs = "$route/{$dokterIdArg}"
    const val title = "Edit Dokter"
}
