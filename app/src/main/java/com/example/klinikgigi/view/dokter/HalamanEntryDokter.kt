package com.example.klinikgigi.view.dokter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
    var spesialisasi by remember { mutableStateOf("") }
    var telp by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val telpValid = telp.isEmpty() || telp.matches(Regex("^08[0-9]{9,11}$"))
    val telpError = if (telp.isNotEmpty() && !telpValid) {
        "Nomor telepon harus diawali 08 dan terdiri dari 11–13 digit"
    } else null

    val isFormValid = nama.isNotBlank() && spesialisasi.isNotBlank() && telpValid && telp.length in 11..13

    val message by viewModel.message.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(message) {
        message?.let { msg ->
            if (msg.contains("berhasil", ignoreCase = true)) {
                showSuccessDialog = true
            } else {
                errorMessage = msg
                showErrorDialog = true
            }
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Dokter Baru", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onKembali) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Lengkapi data dokter di bawah ini:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama Dokter") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = spesialisasi,
                onValueChange = { spesialisasi = it },
                label = { Text("Spesialisasi") },
                placeholder = { Text("Contoh: Orthodontist") },
                leadingIcon = { Icon(Icons.Default.MedicalServices, null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telp,
                onValueChange = { telp = it.filter { c -> c.isDigit() } },
                label = { Text("Nomor Telepon") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                isError = telpError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (telpError != null) {
                Text(
                    text = telpError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.createDokter(
                        Dokter(0, nama, spesialisasi, telp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = isFormValid && !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Simpan Data", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Tidak bisa dismiss, harus klik OK */ },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Sukses") },
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

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Gagal") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}