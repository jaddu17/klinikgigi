package com.example.klinikgigi.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.viewmodel.DokterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    dokterViewModel: DokterViewModel,
    navigateToHalamanDokter: () -> Unit,
    navigateToHalamanPasien: () -> Unit,
    navigateToJanjiTemu: () -> Unit,
    navigateToHalamanTindakan: () -> Unit,
    navigateToHalamanRekamMedis: () -> Unit,   // ✅ TAMBAHAN
    navigateLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Admin Home") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = navigateToHalamanDokter
            ) {
                Text("Kelola Data Dokter")
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = navigateToHalamanPasien
            ) {
                Text("Kelola Data Pasien")
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = navigateToHalamanTindakan
            ) {
                Text("Kelola Data Tindakan")
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = navigateToJanjiTemu
            ) {
                Text("Kelola Janji Temu")
            }

            // ✅ TOMBOL BARU
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = navigateToHalamanRekamMedis
            ) {
                Text("Kelola Rekam Medis")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = navigateLogout
            ) {
                Text("Logout")
            }
        }
    }
}
