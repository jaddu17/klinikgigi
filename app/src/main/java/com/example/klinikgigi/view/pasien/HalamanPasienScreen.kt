package com.example.klinikgigi.view.pasien

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.Pasien
import com.example.klinikgigi.viewmodel.PasienViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanPasienScreen(
    pasienViewModel: PasienViewModel,
    navigateToEntryPasien: () -> Unit,
    navigateToEditPasien: (Int) -> Unit,
    navigateBack: () -> Unit
) {
    val pasienList by pasienViewModel.pasienList.collectAsState()
    val loading by pasienViewModel.loading.collectAsState()
    val searchQuery by pasienViewModel.searchQuery.collectAsState()

    var pasienYangAkanDihapus by remember { mutableStateOf<Pasien?>(null) }

    LaunchedEffect(Unit) {
        pasienViewModel.loadPasien()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data Pasien") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToEntryPasien) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Pasien")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // 🔍 SEARCH BAR PASIEN
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { pasienViewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari pasien berdasarkan nama") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {

                if (pasienList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Data pasien tidak ditemukan")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pasienList) { pasien ->
                            PasienItem(
                                pasien = pasien,
                                onEdit = {
                                    pasien.id_pasien?.let { id ->
                                        navigateToEditPasien(id)
                                    }
                                },
                                onDelete = {
                                    pasienYangAkanDihapus = pasien
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // 🗑️ DIALOG KONFIRMASI HAPUS
    pasienYangAkanDihapus?.let { pasien ->
        AlertDialog(
            onDismissRequest = { pasienYangAkanDihapus = null },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Yakin ingin menghapus pasien \"${pasien.nama_pasien}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pasien.id_pasien?.let { id ->
                            pasienViewModel.deletePasien(id)
                        }
                        pasienYangAkanDihapus = null
                    }
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(onClick = { pasienYangAkanDihapus = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun PasienItem(
    pasien: Pasien,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(pasien.nama_pasien, style = MaterialTheme.typography.titleMedium)
            Text("Jenis Kelamin : ${pasien.jenis_kelamin}")
            Text("Tanggal Lahir : ${pasien.tanggal_lahir}")
            Text("Alamat        : ${pasien.alamat}")
            Text("Telepon       : ${pasien.nomor_telepon}")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) { Text("Edit") }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDelete) { Text("Hapus") }
            }
        }
    }
}
