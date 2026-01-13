package com.example.klinikgigi.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navigateToHalamanDokter: () -> Unit,
    navigateToHalamanPasien: () -> Unit,
    navigateToJanjiTemu: () -> Unit,
    navigateToHalamanTindakan: () -> Unit,
    navigateToHalamanRekamMedis: () -> Unit,
    navigateLogout: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = navigateLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(false, navigateToHalamanDokter,
                    icon = { Icon(Icons.Default.MedicalServices, null) },
                    label = { Text("Dokter") })

                NavigationBarItem(false, navigateToHalamanPasien,
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Pasien") })

                NavigationBarItem(false, navigateToHalamanTindakan,
                    icon = { Icon(Icons.Default.Healing, null) },
                    label = { Text("Tindakan") })

                NavigationBarItem(false, navigateToJanjiTemu,
                    icon = { Icon(Icons.Default.Event, null) },
                    label = { Text("Janji") })

                NavigationBarItem(false, navigateToHalamanRekamMedis,
                    icon = { Icon(Icons.Default.Description, null) },
                    label = { Text("Rekam") })
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            WelcomeCard()

            QuickActionCard(
                "Kelola Pasien",
                "Tambah & lihat data pasien",
                Icons.Default.Person,
                navigateToHalamanPasien
            )

            QuickActionCard(
                "Kelola Janji Temu",
                "Atur jadwal kunjungan",
                Icons.Default.Event,
                navigateToJanjiTemu
            )
        }
    }
}

/* =======================
   COMPONENTS
   ======================= */

@Composable
private fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Selamat Datang 👋",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "Gunakan dashboard ini untuk mengelola sistem Klinik Gigi."
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    desc: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(desc, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


