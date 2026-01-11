package com.example.klinikgigi.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.JanjiTemu
import com.example.klinikgigi.modeldata.JanjiTemuPerDokter
import com.example.klinikgigi.viewmodel.DokterDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JanjiTemuDokterScreen(
    viewModel: DokterDashboardViewModel,
    onBack: () -> Unit
) {
    val jadwal by viewModel.jadwalKlinik.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadJadwalKlinik()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Janji Temu Dokter") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                error != null -> {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(jadwal) { dokter ->
                            DokterExpandableCard(dokter)
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun DokterExpandableCard(
    dokter: JanjiTemuPerDokter
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dokter.nama_dokter,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (expanded)
                        Icons.Default.ExpandLess
                    else
                        Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            if (expanded) {
                Divider()

                if (dokter.janji_temu.isEmpty()) {
                    Text(
                        text = "Tidak ada janji temu",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    dokter.janji_temu.forEach { janji ->
                        JanjiTemuItem(janji)
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun JanjiTemuItem(
    janji: JanjiTemu
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        // ✅ NAMA PASIEN
        Text(
            text = "Pasien: ${janji.nama_pasien ?: "-"}",
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Tanggal: ${janji.tanggal_janji} | ${janji.jam_janji}"
        )

        Text(
            text = "Keluhan: ${janji.keluhan}"
        )

        Text(
            text = "Status: ${janji.status.name.lowercase()}"
        )
    }
}
