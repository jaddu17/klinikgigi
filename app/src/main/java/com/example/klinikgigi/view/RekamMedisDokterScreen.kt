package com.example.klinikgigi.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.RekamMedis
import com.example.klinikgigi.viewmodel.RekamMedisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekamMedisDokterScreen(
    viewModel: RekamMedisViewModel,
    onBack: () -> Unit
) {
    val rekamMedis by viewModel.rekamMedis.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllRekamMedis()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rekam Medis Pasien") },
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

                rekamMedis.isEmpty() -> {
                    Text(
                        text = "Belum ada data rekam medis",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(rekamMedis) { data ->
                            RekamMedisItem(data)
                        }
                    }
                }
            }
        }
    }
}

/* ======================= CARD ITEM ======================= */

@Composable
private fun RekamMedisItem(
    rekam: RekamMedis
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "ID Rekam Medis: ${rekam.id_rekam}",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text("ID Janji Temu: ${rekam.id_janji}")

            Text("Diagnosa: ${rekam.diagnosa}")

            Text("Catatan: ${rekam.catatan}")

            Text("ID Tindakan: ${rekam.id_tindakan}")
        }
    }
}
