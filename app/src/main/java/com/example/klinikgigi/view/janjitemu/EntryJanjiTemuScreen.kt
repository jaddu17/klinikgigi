package com.example.klinikgigi.view.janjitemu

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import android.app.TimePickerDialog
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryJanjiTemuScreen(
    viewModel: JanjiTemuViewModel,
    janjiId: Int?,
    navigateBack: () -> Unit
) {
    val dokterList by viewModel.dokterList.collectAsState()
    val pasienList by viewModel.pasienList.collectAsState()
    val selectedJanji by viewModel.selectedJanji.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val statusMsg by viewModel.status.collectAsState()

    var selectedDokterId by remember { mutableStateOf<Int?>(null) }
    var selectedPasienId by remember { mutableStateOf<Int?>(null) }
    var dokterSearchQuery by remember { mutableStateOf("") }
    var pasienSearchQuery by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var jam by remember { mutableStateOf("") }
    var keluhan by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(StatusJanji.KONFIRMASI) }
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val statusList = StatusJanji.values().toList()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(selectedJanji, dokterList, pasienList) {
        selectedJanji?.let {
            selectedDokterId = it.id_dokter
            selectedPasienId = it.id_pasien
            tanggal = it.tanggal_janji
            jam = it.jam_janji
            keluhan = it.keluhan
            status = it.status
            
            // Initialize search queries with names
            dokterSearchQuery = dokterList.find { d -> d.id_dokter == it.id_dokter }?.nama_dokter ?: ""
            pasienSearchQuery = pasienList.find { p -> p.id_pasien == it.id_pasien }?.nama_pasien ?: ""
        }
    }

    LaunchedEffect(statusMsg) {
        statusMsg?.let {
            if (it.contains("berhasil", ignoreCase = true)) {
                showSuccessDialog = true
            } else {
                errorMessage = it
                showErrorDialog = true
            }
            viewModel.clearStatus()
        }
    }

    // Validation
    val isFormValid = selectedDokterId != null && 
                      selectedPasienId != null && 
                      tanggal.isNotBlank() && 
                      jam.isNotBlank() &&
                      keluhan.isNotBlank()

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

    fun openTimePicker() {
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, hour, minute ->
                jam = "%02d:%02d".format(hour, minute)
            },
            currentHour,
            currentMinute,
            true // 24 hour format
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (janjiId == null) "Buat Janji Temu" else "Edit Janji Temu", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Dropdown Dokter (Searchable)
            var expandedDokter by remember { mutableStateOf(false) }
            val filteredDokter = dokterList.filter { 
                it.nama_dokter.contains(dokterSearchQuery, ignoreCase = true) 
            }
            
            ExposedDropdownMenuBox(
                expanded = expandedDokter,
                onExpandedChange = { expandedDokter = !expandedDokter }
            ) {
                OutlinedTextField(
                    value = dokterSearchQuery,
                    onValueChange = { 
                        dokterSearchQuery = it
                        expandedDokter = true
                        // Reset ID if query is changed from the selected name
                        val selectedName = dokterList.find { d -> d.id_dokter == selectedDokterId }?.nama_dokter
                        if (it != selectedName) {
                            selectedDokterId = null
                        }
                    },
                    label = { Text("Pilih Dokter") },
                    leadingIcon = { Icon(Icons.Default.MedicalServices, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedDokter) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    placeholder = { Text("Ketik nama dokter...") }
                )
                
                if (filteredDokter.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expandedDokter, 
                        onDismissRequest = { expandedDokter = false }
                    ) {
                        filteredDokter.forEach { 
                            DropdownMenuItem(
                                text = { Text(it.nama_dokter) },
                                onClick = { 
                                    selectedDokterId = it.id_dokter
                                    dokterSearchQuery = it.nama_dokter
                                    expandedDokter = false 
                                }
                            ) 
                        }
                    }
                }
            }

            // Dropdown Pasien (Searchable)
            var expandedPasien by remember { mutableStateOf(false) }
            val filteredPasien = pasienList.filter { 
                it.nama_pasien.contains(pasienSearchQuery, ignoreCase = true) 
            }
            
            ExposedDropdownMenuBox(
                expanded = expandedPasien,
                onExpandedChange = { expandedPasien = !expandedPasien }
            ) {
                OutlinedTextField(
                    value = pasienSearchQuery,
                    onValueChange = { 
                        pasienSearchQuery = it
                        expandedPasien = true
                        // Reset ID if query is changed from the selected name
                        val selectedName = pasienList.find { p -> p.id_pasien == selectedPasienId }?.nama_pasien
                        if (it != selectedName) {
                            selectedPasienId = null
                        }
                    },
                    label = { Text("Pilih Pasien") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedPasien) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    placeholder = { Text("Ketik nama pasien...") }
                )
                
                if (filteredPasien.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expandedPasien, 
                        onDismissRequest = { expandedPasien = false }
                    ) {
                        filteredPasien.forEach { 
                            DropdownMenuItem(
                                text = { Text(it.nama_pasien) },
                                onClick = { 
                                    selectedPasienId = it.id_pasien
                                    pasienSearchQuery = it.nama_pasien
                                    expandedPasien = false 
                                }
                            ) 
                        }
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
                onValueChange = {},
                readOnly = true,
                label = { Text("Jam") },
                leadingIcon = { Icon(Icons.Default.Schedule, null) },
                trailingIcon = { IconButton(onClick = { openTimePicker() }) { Icon(Icons.Default.EditCalendar, null) } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().clickable { openTimePicker() }
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

            // Status (hanya tampil jika edit / id != null)
            if (janjiId != null) {
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
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val janji = JanjiTemu(
                        id_janji = janjiId ?: 0,
                        id_dokter = selectedDokterId!!,
                        id_pasien = selectedPasienId!!,
                        tanggal_janji = tanggal,
                        jam_janji = jam,
                        keluhan = keluhan,
                        status = status
                    )
                    if (janjiId == null) viewModel.createJanjiTemu(janji) else viewModel.updateJanjiTemu(janji)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = isFormValid && !loading
            ) {
                if (loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                else Text("Simpan Data", fontWeight = FontWeight.Bold)
            }
        }
    }

     if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Sukses") },
            text = { Text("Data janji temu berhasil disimpan!") },
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