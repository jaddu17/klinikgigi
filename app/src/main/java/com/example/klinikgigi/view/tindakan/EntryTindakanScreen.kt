package com.example.klinikgigi.view.tindakan

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.Tindakan
import com.example.klinikgigi.viewmodel.TindakanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryTindakanScreen(
    viewModel: TindakanViewModel,
    onBack: () -> Unit
) {
    var namaTindakan by remember { mutableStateOf("") }
    var harga by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entry Tindakan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = namaTindakan,
                onValueChange = { namaTindakan = it },
                label = { Text("Nama Tindakan") },
                modifier = Modifier.fillMaxWidth()
            )


            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = harga,
                onValueChange = { harga = it },
                label = { Text("harga") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (namaTindakan.isBlank() || harga.isBlank() || deskripsi.isBlank()) {
                        showError = true
                    } else {
                        viewModel.createTindakan(
                            Tindakan(
                                nama_tindakan = namaTindakan,
                                deskripsi = deskripsi,
                                harga = harga
                            )
                        )
                        showSuccess = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan")
            }
        }
    }

    // ---------- Dialog Error ----------
    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("Peringatan") },
            text = { Text("Semua field harus diisi!") },
            confirmButton = {
                Button(onClick = { showError = false }) { Text("OK") }
            }
        )
    }

    // ---------- Dialog Success ----------
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            title = { Text("Berhasil") },
            text = { Text("Data tindakan berhasil disimpan!") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccess = false
                        onBack()
                    }
                ) { Text("OK") }
            }
        )
    }
}
