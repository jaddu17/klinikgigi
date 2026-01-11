package com.example.klinikgigi.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.klinikgigi.modeldata.Dokter
import com.example.klinikgigi.viewmodel.DokterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEntryDokter(
    viewModel: DokterViewModel,
    onSelesai: () -> Unit,
    onKembali: () -> Unit
) {
    var nama by remember { mutableStateOf("") }
    var spesialis by remember { mutableStateOf("") }
    var telp by remember { mutableStateOf("") }

    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Masukkan Data Dokter") },
                navigationIcon = {
                    IconButton(onClick = onKembali) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp),
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
                value = telp,
                onValueChange = { telp = it },
                label = { Text("Nomor Telepon") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (nama.isBlank() || spesialis.isBlank() || telp.isBlank()) {
                        showErrorDialog = true
                    } else {
                        viewModel.createDokter(
                            Dokter(
                                id_dokter = 0,          // ✅ FIX UTAMA
                                nama_dokter = nama,
                                spesialisasi = spesialis,
                                nomor_telepon = telp
                            )
                        )
                        showSuccessDialog = true
                    }
                }
            ) {
                Text("Simpan")
            }
        }
    }

    // ===== ERROR DIALOG =====
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Peringatan") },
            text = { Text("Semua data dokter harus diisi!") },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // ===== SUCCESS DIALOG =====
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Berhasil") },
            text = { Text("Data dokter berhasil disimpan!") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onSelesai()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
