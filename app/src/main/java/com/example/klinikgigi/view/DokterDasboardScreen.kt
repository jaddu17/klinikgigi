package com.example.klinikgigi.view.dokter

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DokterHomeScreen(
    navigateToRekamMedis: () -> Unit,
    navigateToJanjiTemu: () -> Unit,
    navigateLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Dokter") }
            )
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
                onClick = navigateToRekamMedis
            ) {
                Text("Rekam Medis")
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = navigateToJanjiTemu
            ) {
                Text("Janji Temu")
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
