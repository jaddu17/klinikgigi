package com.example.klinikgigi.view

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.JanjiTemu
import com.example.klinikgigi.modeldata.StatusJanji
import com.example.klinikgigi.viewmodel.JanjiTemuViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJanjiTemuScreen(
    viewModel: JanjiTemuViewModel,
    janjiId: Int,
    navigateBack: () -> Unit
) {
    val dokterList by viewModel.dokterList.collectAsState()
    val pasienList by viewModel.pasienList.collectAsState()
    val selectedJanji by viewModel.selectedJanji.collectAsState()

    val loading by viewModel.loading.collectAsState()
    val statusMsg by viewModel.status.collectAsState()

    // State Form
    var selectedDokterId by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedPasienId by rememberSaveable { mutableStateOf<Int?>(null) }
    var tanggal by rememberSaveable { mutableStateOf("") }
    var jam by rememberSaveable { mutableStateOf("") }
    var keluhan by rememberSaveable { mutableStateOf("") }

    // STATUS ENUM
    var status by rememberSaveable { mutableStateOf(StatusJanji.MENUNGGU) }
    val statusList = listOf(StatusJanji.MENUNGGU, StatusJanji.SELESAI, StatusJanji.BATAL)

    var showSuccessDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Load data by ID
    LaunchedEffect(janjiId) {
        viewModel.loadJanjiById(janjiId)
    }

    // Isi form ketika selectedJanji berubah
    LaunchedEffect(selectedJanji) {
        selectedJanji?.let { janji ->
            selectedDokterId = janji.id_dokter
            selectedPasienId = janji.id_pasien
            tanggal = janji.tanggal_janji
            jam = janji.jam_janji
            keluhan = janji.keluhan
            status = janji.status
        }
    }

    // Alert sukses update
    LaunchedEffect(statusMsg) {
        if (statusMsg?.contains("Berhasil update") == true) {
            showSuccessDialog = true
            viewModel.clearStatus()
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Sukses") },
            text = { Text("Data janji temu berhasil diperbarui") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    navigateBack()
                }) {
                    Text("OK")
                }
            }
        )
    }

    // Date Picker
    fun openDatePicker() {
        val today = Calendar.getInstance()
        val max = today.clone() as Calendar
        max.add(Calendar.DAY_OF_YEAR, 7)

        DatePickerDialog(
            context,
            { _, y, m, d ->
                tanggal = "%04d-%02d-%02d".format(y, m + 1, d)
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = today.timeInMillis
            datePicker.maxDate = max.timeInMillis
        }.show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Janji Temu") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ================== DOKTER ==================
            var expandedDokter by remember { mutableStateOf(false) }
            val selectedDokter = dokterList.find { it.id_dokter == selectedDokterId }

            ExposedDropdownMenuBox(
                expanded = expandedDokter,
                onExpandedChange = { expandedDokter = !expandedDokter }
            ) {
                OutlinedTextField(
                    value = selectedDokter?.nama_dokter ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Dokter") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedDokter) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedDokter,
                    onDismissRequest = { expandedDokter = false }
                ) {
                    dokterList.forEach {
                        DropdownMenuItem(
                            text = { Text("${it.nama_dokter} (${it.spesialisasi})") },
                            onClick = {
                                selectedDokterId = it.id_dokter
                                expandedDokter = false
                            }
                        )
                    }
                }
            }

            // ================== PASIEN ==================
            var expandedPasien by remember { mutableStateOf(false) }
            val selectedPasien = pasienList.find { it.id_pasien == selectedPasienId }

            ExposedDropdownMenuBox(
                expanded = expandedPasien,
                onExpandedChange = { expandedPasien = !expandedPasien }
            ) {
                OutlinedTextField(
                    value = selectedPasien?.nama_pasien ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pasien") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedPasien) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedPasien,
                    onDismissRequest = { expandedPasien = false }
                ) {
                    pasienList.forEach {
                        DropdownMenuItem(
                            text = { Text(it.nama_pasien) },
                            onClick = {
                                selectedPasienId = it.id_pasien
                                expandedPasien = false
                            }
                        )
                    }
                }
            }

            // ================== TANGGAL ==================
            OutlinedTextField(
                value = tanggal,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal Janji") },
                trailingIcon = {
                    IconButton(onClick = { openDatePicker() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { openDatePicker() }
            )

            // ================== JAM ==================
            OutlinedTextField(
                value = jam,
                onValueChange = { jam = it },
                label = { Text("Jam (HH:MM)") },
                modifier = Modifier.fillMaxWidth()
            )

            // ================== KELUHAN ==================
            OutlinedTextField(
                value = keluhan,
                onValueChange = { keluhan = it },
                label = { Text("Keluhan Pasien") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            // ================== STATUS ==================
            var expandedStatus by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedStatus,
                onExpandedChange = { expandedStatus = !expandedStatus }
            ) {
                OutlinedTextField(
                    value = status.toString().replaceFirstChar { it.titlecase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedStatus) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedStatus,
                    onDismissRequest = { expandedStatus = false }
                ) {
                    statusList.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.toString().replaceFirstChar { it.titlecase() }) },
                            onClick = {
                                status = item
                                expandedStatus = false
                            }
                        )
                    }
                }
            }

            // ================== BUTTON ==================
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedDokterId != null &&
                        selectedPasienId != null &&
                        tanggal.isNotBlank() &&
                        jam.isNotBlank(),
                onClick = {
                    val updated = JanjiTemu(
                        id = selectedJanji!!.id,
                        id_dokter = selectedDokterId!!,
                        id_pasien = selectedPasienId!!,
                        tanggal_janji = tanggal,
                        jam_janji = jam,
                        keluhan = keluhan,
                        status = status     // ✔ Paling penting, kirim ENUM benar
                    )
                    viewModel.updateJanjiTemu(updated)
                }
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Simpan Perubahan")
                }
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = navigateBack
            ) {
                Text("Batal")
            }
        }
    }
}
