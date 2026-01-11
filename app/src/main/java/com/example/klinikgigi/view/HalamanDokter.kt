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
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.Dokter
import com.example.klinikgigi.viewmodel.DokterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDokter(
    viewModel: DokterViewModel,
    onTambah: () -> Unit,
    onEdit: (Int) -> Unit,
    onBack: () -> Unit
) {
    val dokterList by viewModel.dokterList.collectAsState()
    val loading by viewModel.loading.collectAsState()

    // State untuk AlertDialog hapus
    var dokterYangAkanDihapus by remember { mutableStateOf<Dokter?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadDokter()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data Dokter") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onTambah) {
                Text("+")
            }
        }
    ) { padding ->

        when {
            loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            dokterList.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Belum ada data dokter") }

            else -> LazyColumn(
                Modifier.padding(padding).padding(16.dp)
            ) {
                items(dokterList) { dokter ->
                    DokterItem(
                        dokter = dokter,
                        onEdit = {
                            dokter.id_dokter?.let { id ->
                                onEdit(id)
                            }
                        },
                        onDelete = {
                            dokterYangAkanDihapus = dokter // simpan dokter, bukan hanya ID
                        }
                    )
                }
            }
        }
    }

    // 💥 AlertDialog Konfirmasi Hapus
    dokterYangAkanDihapus?.let { dokter ->
        AlertDialog(
            onDismissRequest = { dokterYangAkanDihapus = null },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Yakin ingin menghapus dokter \"${dokter.nama_dokter}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        dokter.id_dokter?.let { id ->
                            viewModel.deleteDokter(id)
                        }
                        dokterYangAkanDihapus = null
                    }
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(onClick = { dokterYangAkanDihapus = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun DokterItem(
    dokter: Dokter,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Nama Dokter : ${dokter.nama_dokter}")
            Text("Spesialisasi : ${dokter.spesialisasi}")
            Text("Telepon   : ${dokter.nomor_telepon}")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) { Text("Edit") }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onDelete) { Text("Hapus") }
            }
        }
    }
}