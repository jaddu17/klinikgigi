package com.example.klinikgigi.view.tindakan

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.Tindakan
import com.example.klinikgigi.viewmodel.TindakanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTindakanScreen(
    tindakanId: Int,
    viewModel: TindakanViewModel,
    onBack: () -> Unit
) {
    // Load data berdasarkan ID
    LaunchedEffect(tindakanId) {
        viewModel.loadTindakanById(tindakanId)
    }

    // State
    val tindakan by viewModel.selectedTindakan.collectAsState()

    var nama by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var harga by remember { mutableStateOf("") }

    // Ketika data dari viewmodel masuk → isi form
    LaunchedEffect(tindakan) {
        tindakan?.let {
            nama = it.nama_tindakan
            deskripsi = it.deskripsi
            harga = it.harga
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Tindakan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->

        if (tindakan == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
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

            Button(
                onClick = {
                    val updated = Tindakan(
                        id_tindakan = tindakanId,
                        nama_tindakan = nama,
                        deskripsi = deskripsi,
                        harga = harga
                    )

                    viewModel.updateTindakan(updated)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Perubahan")
            }
        }
    }
}
