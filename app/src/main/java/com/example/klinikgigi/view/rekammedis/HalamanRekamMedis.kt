package com.example.klinikgigi.view.rekammedis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.RekamMedis
import com.example.klinikgigi.viewmodel.RekamMedisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanRekamMedis(
    viewModel: RekamMedisViewModel,
    onEdit: (Int) -> Unit,
    onBack: () -> Unit
) {
    val rekamMedisList by viewModel.rekamMedisList.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var rekamMedisHapus by remember { mutableStateOf<RekamMedis?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadRekamMedis()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rekam Medis") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->

        when {
            loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            rekamMedisList.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada data rekam medis")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(rekamMedisList) { rm ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("ID Janji     : ${rm.id_janji}")
                                Text("ID Tindakan  : ${rm.id_tindakan}")
                                Text("Diagnosa     : ${rm.diagnosa}")
                                Text("Catatan      : ${rm.catatan}")
                                Text("Resep        : ${rm.resep}")

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = { onEdit(rm.id_rekam) }
                                    ) {
                                        Text("Edit")
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    TextButton(
                                        onClick = { rekamMedisHapus = rm }
                                    ) {
                                        Text("Hapus")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ================= KONFIRMASI HAPUS =================
    rekamMedisHapus?.let { rm ->
        AlertDialog(
            onDismissRequest = { rekamMedisHapus = null },
            title = { Text("Hapus Rekam Medis") },
            text = {
                Text("Yakin ingin menghapus rekam medis untuk janji ID ${rm.id_janji}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRekamMedis(rm.id_rekam)
                        rekamMedisHapus = null
                    }
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(onClick = { rekamMedisHapus = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
