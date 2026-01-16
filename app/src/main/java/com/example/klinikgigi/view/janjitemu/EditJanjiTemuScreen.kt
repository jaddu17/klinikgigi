package com.example.klinikgigi.view.janjitemu

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.JanjiTemu
import com.example.klinikgigi.modeldata.StatusJanji
import com.example.klinikgigi.viewmodel.JanjiTemuViewModel
import kotlinx.coroutines.launch
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

    var selectedDokterId by remember { mutableStateOf<Int?>(null) }
    var selectedPasienId by remember { mutableStateOf<Int?>(null) }
    var tanggal by remember { mutableStateOf("") }
    var jam by remember { mutableStateOf("") }
    var keluhan by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(StatusJanji.KONFIRMASI) }

    var showSuccessDialog by remember { mutableStateOf(false) }

    val statusList = StatusJanji.values().toList()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(janjiId) {
        viewModel.loadJanjiById(janjiId)
    }

    LaunchedEffect(selectedJanji) {
        selectedJanji?.let {
            selectedDokterId = it.id_dokter
            selectedPasienId = it.id_pasien
            tanggal = it.tanggal_janji
            jam = it.jam_janji
            keluhan = it.keluhan
            status = it.status
        }
    }

    LaunchedEffect(statusMsg) {
        statusMsg?.let {
            if (it.contains("berhasil", ignoreCase = true)) {
                showSuccessDialog = true
            } else {
                scope.launch { snackbarHostState.showSnackbar(it) }
            }
            viewModel.clearStatus()
        }
    }

    val isChanged = selectedJanji?.let {
        it.id_dokter != selectedDokterId ||
                it.id_pasien != selectedPasienId ||
                it.tanggal_janji != tanggal ||
                it.jam_janji != jam ||
                it.keluhan != keluhan ||
                it.status != status
    } ?: false

    val isFormValid = selectedDokterId != null && selectedPasienId != null && tanggal.isNotBlank() && jam.isNotBlank() && keluhan.isNotBlank()

    // DatePicker
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    fun openDatePicker() {
        DatePickerDialog(
            context,
            { _, y, m, d -> tanggal = "%04d-%02d-%02d".format(y, m + 1, d) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Janji Temu", fontWeight = FontWeight.Bold) },
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
        if (selectedJanji == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Dropdown Dokter
            var expandedDokter by remember { mutableStateOf(false) }
            val selectedDokterName = dokterList.find { it.id_dokter == selectedDokterId }?.nama_dokter ?: ""
            ExposedDropdownMenuBox(
                expanded = expandedDokter,
                onExpandedChange = { expandedDokter = !expandedDokter }
            ) {
                OutlinedTextField(
                    value = selectedDokterName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Dokter") },
                    leadingIcon = { Icon(Icons.Default.MedicalServices, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedDokter) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedDokter, onDismissRequest = { expandedDokter = false }) {
                    dokterList.forEach {
                        DropdownMenuItem(
                            text = { Text(it.nama_dokter) },
                            onClick = { selectedDokterId = it.id_dokter; expandedDokter = false }
                        )
                    }
                }
            }

            // Dropdown Pasien
            var expandedPasien by remember { mutableStateOf(false) }
            val selectedPasienName = pasienList.find { it.id_pasien == selectedPasienId }?.nama_pasien ?: ""
            ExposedDropdownMenuBox(
                expanded = expandedPasien,
                onExpandedChange = { expandedPasien = !expandedPasien }
            ) {
                OutlinedTextField(
                    value = selectedPasienName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Pasien") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedPasien) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedPasien, onDismissRequest = { expandedPasien = false }) {
                    pasienList.forEach {
                        DropdownMenuItem(
                            text = { Text(it.nama_pasien) },
                            onClick = { selectedPasienId = it.id_pasien; expandedPasien = false }
                        )
                    }
                }
            }

            // Tanggal
            OutlinedTextField(
                value = tanggal,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal") },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                trailingIcon = { IconButton(onClick = { openDatePicker() }) { Icon(Icons.Default.EditCalendar, null) } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().clickable { openDatePicker() }
            )

            // Jam
            OutlinedTextField(
                value = jam,
                onValueChange = { jam = it },
                label = { Text("Jam (Contoh: 10:00)") },
                leadingIcon = { Icon(Icons.Default.Schedule, null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Keluhan
            OutlinedTextField(
                value = keluhan,
                onValueChange = { keluhan = it },
                label = { Text("Keluhan") },
                leadingIcon = { Icon(Icons.Default.Info, null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            // Status Dropdown
            var expandedStatus by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedStatus,
                onExpandedChange = { expandedStatus = !expandedStatus }
            ) {
                OutlinedTextField(
                    value = status.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedStatus) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedStatus, onDismissRequest = { expandedStatus = false }) {
                    statusList.forEach {
                        DropdownMenuItem(
                            text = { Text(it.name) },
                            onClick = { status = it; expandedStatus = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val updated = JanjiTemu(
                        id_janji = janjiId,
                        id_dokter = selectedDokterId!!,
                        id_pasien = selectedPasienId!!,
                        tanggal_janji = tanggal,
                        jam_janji = jam,
                        keluhan = keluhan,
                        status = status
                    )
                    viewModel.updateJanjiTemu(updated)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = isFormValid && isChanged && !loading
            ) {
                if (loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                else Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Sukses") },
            text = { Text("Data janji temu berhasil diperbarui!") },
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
}