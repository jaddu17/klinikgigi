package com.example.klinikgigi.view.pasien

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.Pasien
import com.example.klinikgigi.viewmodel.PasienViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryPasienScreen(
    pasienViewModel: PasienViewModel,
    pasienId: Int?,
    navigateBack: () -> Unit
) {

    LaunchedEffect(pasienId) {
        pasienId?.let {
            pasienViewModel.loadPasienById(it)
        }
    }

    val pasien by pasienViewModel.selectedPasien.collectAsState()
    val loading by pasienViewModel.loading.collectAsState()
    val message by pasienViewModel.message.collectAsState()

    var nama by remember { mutableStateOf("") }
    var jenisKelamin by remember { mutableStateOf("") }
    var tanggalLahir by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var nomorTelepon by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Populate fields when editing
    LaunchedEffect(pasien) {
        pasien?.let {
            nama = it.nama_pasien
            jenisKelamin = it.jenis_kelamin
            tanggalLahir = it.tanggal_lahir
            alamat = it.alamat
            alamat = it.alamat
            nomorTelepon = it.nomor_telepon
        }
    }

    // Handle messages
    LaunchedEffect(message) {
        message?.let { msg ->
            if (msg.contains("berhasil", ignoreCase = true)) {
                showSuccessDialog = true
            } else {
                errorMessage = msg
                showErrorDialog = true
            }
            pasienViewModel.clearMessage()
        }
    }

    // Validation
    val telpValid = nomorTelepon.isEmpty() || nomorTelepon.matches(Regex("^08[0-9]{9,11}$"))
    val telpError = if (nomorTelepon.isNotEmpty() && !telpValid) {
        "Nomor telepon harus diawali 08 dan terdiri dari 11–13 digit"
    } else null

    val isFormValid = nama.isNotBlank() &&
            jenisKelamin.isNotBlank() &&
            tanggalLahir.isNotBlank() &&
            alamat.isNotBlank() &&
            telpValid &&
            telpValid &&
            nomorTelepon.length in 11..13

    // DatePicker
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    fun openDatePicker() {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                tanggalLahir = "%04d-%02d-%02d".format(year, month + 1, day)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }.show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (pasienId == null) "Tambah Pasien" else "Edit Pasien", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama Pasien") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Jenis Kelamin", style = MaterialTheme.typography.bodyMedium)
            Row {
                RadioButton(selected = jenisKelamin == "Laki-laki", onClick = { jenisKelamin = "Laki-laki" })
                Text("Laki-laki", modifier = Modifier.padding(top = 12.dp))
                Spacer(Modifier.width(16.dp))
                RadioButton(selected = jenisKelamin == "Perempuan", onClick = { jenisKelamin = "Perempuan" })
                Text("Perempuan", modifier = Modifier.padding(top = 12.dp))
            }

            OutlinedTextField(
                value = tanggalLahir,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal Lahir") },
                trailingIcon = {
                    IconButton(onClick = { openDatePicker() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openDatePicker() }
            )

            OutlinedTextField(
                value = alamat,
                onValueChange = { alamat = it },
                label = { Text("Alamat") },
                leadingIcon = { Icon(Icons.Default.Home, null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nomorTelepon,
                onValueChange = { nomorTelepon = it.filter { c -> c.isDigit() } },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = isFormValid && !loading,
                onClick = {
                    val p = Pasien(
                        id_pasien = pasienId ?: 0,
                        nama_pasien = nama,
                        jenis_kelamin = jenisKelamin,
                        tanggal_lahir = tanggalLahir,
                        alamat = alamat,
                        nomor_telepon = nomorTelepon
                    )
                    if (pasienId == null) {
                        pasienViewModel.createPasien(p)
                    } else {
                        pasienViewModel.updatePasien(p)
                    }
                }
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (pasienId == null) "Simpan Data" else "Update Data", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Sukses") },
            text = { Text(if (pasienId == null) "Data pasien berhasil disimpan!" else "Data pasien berhasil diperbarui!") },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    navigateBack()
                }) {
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