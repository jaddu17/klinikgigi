package com.example.klinikgigi.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.viewmodel.DokterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDokterScreen(
    dokterId: Int,
    viewModel: DokterViewModel,
    onBack: () -> Unit
) {
    // Pastikan data dokter sudah ada
    LaunchedEffect(Unit) {
        viewModel.loadDokter()
    }

    val dokterList by viewModel.dokterList.collectAsState()

    // Cari dokter berdasarkan ID
    val dokter = dokterList.find { it.id_dokter == dokterId }

    var nama by remember { mutableStateOf("") }
    var spesialis by remember { mutableStateOf("") }
    var telepon by remember { mutableStateOf("") }

    // Saat data dokter sudah ditemukan → isi form
    LaunchedEffect(dokter) {
        dokter?.let {
            nama = it.nama_dokter
            spesialis = it.spesialisasi
            telepon = it.nomor_telepon
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Dokter") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->

        if (dokter == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama Dokter") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = spesialis,
                onValueChange = { spesialis = it },
                label = { Text("Spesialisasi") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telepon,
                onValueChange = { telepon = it },
                label = { Text("Nomor Telepon") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val updated = dokter.copy(
                        nama_dokter = nama,
                        spesialisasi = spesialis,
                        nomor_telepon = telepon
                    )
                    viewModel.updateDokter(updated)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Perubahan")
            }
        }
    }
}
