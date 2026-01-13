package com.example.klinikgigi.view.pasien

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.Pasien
import com.example.klinikgigi.viewmodel.PasienViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPasienScreen(
    pasienId: Int,
    pasienViewModel: PasienViewModel,
    navigateBack: () -> Unit
) {

    // Load pasien by ID
    LaunchedEffect(pasienId) {
        pasienViewModel.loadPasienById(pasienId)
    }

    val pasien by pasienViewModel.selectedPasien.collectAsState()

    // STATE FORM
    var nama by remember { mutableStateOf("") }
    var jenisKelamin by remember { mutableStateOf("") }
    var tanggalLahir by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var nomorTelepon by remember { mutableStateOf("") }

    // Ketika data pasien masuk
    LaunchedEffect(pasien) {
        pasien?.let {
            nama = it.nama_pasien
            jenisKelamin = it.jenis_kelamin
            tanggalLahir = it.tanggal_lahir
            alamat = it.alamat
            nomorTelepon = it.nomor_telepon
        }
    }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Jika tanggal sudah ada → set datepicker ke tanggal itu
    if (tanggalLahir.isNotEmpty()) {
        try {
            val parts = tanggalLahir.split("-")
            calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
        } catch (_: Exception) { }
    }

    // ====== DATE PICKER DIALOG ======
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val m = (month + 1).toString().padStart(2, '0')
                val d = day.toString().padStart(2, '0')
                tanggalLahir = "$year-$m-$d"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()  // ❌ Tidak bisa pilih tanggal setelah hari ini
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Data Pasien") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->

        if (pasien == null) {
            Box(
                Modifier
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
                label = { Text("Nama Pasien") },
                modifier = Modifier.fillMaxWidth()
            )

            // ===================== RADIO JENIS KELAMIN =====================
            Text("Jenis Kelamin", style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {

                RadioButton(
                    selected = jenisKelamin == "Laki-laki",
                    onClick = { jenisKelamin = "Laki-laki" }
                )
                Text("Laki-laki")

                Spacer(Modifier.width(24.dp))

                RadioButton(
                    selected = jenisKelamin == "Perempuan",
                    onClick = { jenisKelamin = "Perempuan" }
                )
                Text("Perempuan")
            }

            // ===================== DATE PICKER FIELD =====================
            OutlinedTextField(
                value = tanggalLahir,
                onValueChange = {},
                label = { Text("Tanggal Lahir") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                trailingIcon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = "Pilih Tanggal",
                        modifier = Modifier.clickable { datePickerDialog.show() }
                    )
                }
            )

            OutlinedTextField(
                value = alamat,
                onValueChange = { alamat = it },
                label = { Text("Alamat") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nomorTelepon,
                onValueChange = { nomorTelepon = it },
                label = { Text("Nomor Telepon") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val updated = Pasien(
                        id_pasien = pasienId,
                        nama_pasien = nama,
                        jenis_kelamin = jenisKelamin,
                        tanggal_lahir = tanggalLahir,
                        alamat = alamat,
                        nomor_telepon = nomorTelepon
                    )
                    pasienViewModel.updatePasien(updated)
                    navigateBack()
                }
            ) {
                Text("Update Data")
            }
        }
    }
}
