package com.example.klinikgigi.view.dokter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.klinikgigi.modeldata.Dokter
import com.example.klinikgigi.viewmodel.DokterViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEntryDokter(
    viewModel: DokterViewModel,
    onSelesai: () -> Unit,
    onKembali: () -> Unit
) {
    var nama by remember { mutableStateOf("") }
    var spesialisasi by remember { mutableStateOf("") }
    var telp by remember { mutableStateOf("") }

    val telpValid = telp.isEmpty() || telp.matches(Regex("^08[0-9]{9,11}$"))
    val telpError = if (telp.isNotEmpty() && !telpValid) {
        "Nomor telepon harus diawali 08 dan terdiri dari 11–13 digit"
    } else null

    val message by viewModel.message.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(message) {
        message?.let { msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
            if (msg.contains("berhasil", ignoreCase = true)) {
                kotlinx.coroutines.delay(1000)
                onSelesai()
            }
            viewModel.clearMessage()
        }
    }

    // Form valid jika semua terisi + telp valid
    val isFormValid = nama.isNotBlank() &&
            spesialisasi.isNotBlank() &&
            telpValid &&
            telp.length in 11..13

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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama Dokter") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = spesialisasi,
                onValueChange = { spesialisasi = it },
                label = { Text("Spesialisasi") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telp,
                onValueChange = { telp = it.filter { char -> char.isDigit() } }, // Hanya angka
                label = { Text("Nomor Telepon") },
                isError = telpError != null,
                modifier = Modifier.fillMaxWidth()
            )

            // Tampilkan error di bawah TextField
            if (telpError != null) {
                Text(
                    text = telpError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    when {
                        nama.isBlank() -> scope.launch {
                            snackbarHostState.showSnackbar("Nama dokter wajib diisi")
                        }
                        spesialisasi.isBlank() -> scope.launch {
                            snackbarHostState.showSnackbar("Spesialisasi wajib diisi")
                        }
                        !telpValid || telp.length !in 11..13 -> scope.launch {
                            snackbarHostState.showSnackbar("Nomor telepon tidak valid")
                        }
                        else -> {
                            viewModel.createDokter(
                                Dokter(
                                    id_dokter = 0,
                                    nama_dokter = nama,
                                    spesialisasi = spesialisasi,
                                    nomor_telepon = telp
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid && !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Simpan")
                }
            }
        }
    }
}