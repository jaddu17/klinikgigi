package com.example.klinikgigi.view.rekammedis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.RekamMedis
import com.example.klinikgigi.viewmodel.RekamMedisViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryRekamMedisScreen(
    viewModel: RekamMedisViewModel,
    rekamMedisId: Int?,
    idJanji: Int, // 🔒 WAJIB & TIDAK BISA DIUBAH
    navigateBack: () -> Unit
) {

    LaunchedEffect(rekamMedisId) {
        rekamMedisId?.let { viewModel.loadRekamMedisById(it) }
        viewModel.loadTindakan()
    }

    val rekamMedis by viewModel.selectedRekamMedis.collectAsState()
    val tindakanList by viewModel.tindakanList.collectAsState()
    val statusMsg by viewModel.status.collectAsState()
    val success by viewModel.success.collectAsState()

    var idTindakan by remember { mutableStateOf<Int?>(null) }
    var diagnosa by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }
    var resep by remember { mutableStateOf("") }
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 🔁 Mode edit
    LaunchedEffect(rekamMedis) {
        rekamMedis?.let {
            idTindakan = it.id_tindakan
            diagnosa = it.diagnosa
            catatan = it.catatan
            resep = it.resep
        }
    }
    
    LaunchedEffect(statusMsg) {
        statusMsg?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.clearStatus()
        }
    }

    LaunchedEffect(success) {
        if (success) {
            showSuccessDialog = true
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (rekamMedisId == null) "Tambah Rekam Medis" else "Edit Rekam Medis",
                        fontWeight = FontWeight.Bold 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ================= JANJI (LOCKED) =================
            OutlinedTextField(
                value = "ID Janji: $idJanji",
                onValueChange = {},
                label = { Text("Janji Temu") },
                enabled = false,
                readOnly = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // ================= TINDAKAN =================
            var expandTindakan by remember { mutableStateOf(false) }
            val selectedTindakan = tindakanList.firstOrNull { it.id_tindakan == idTindakan }

            ExposedDropdownMenuBox(
                expanded = expandTindakan,
                onExpandedChange = { expandTindakan = !expandTindakan }
            ) {
                OutlinedTextField(
                    value = selectedTindakan?.nama_tindakan ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Tindakan") },
                    leadingIcon = { Icon(Icons.Default.MedicalServices, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandTindakan) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandTindakan,
                    onDismissRequest = { expandTindakan = false }
                ) {
                    tindakanList.forEach { tindakan ->
                        DropdownMenuItem(
                            text = { Text(tindakan.nama_tindakan) },
                            onClick = {
                                idTindakan = tindakan.id_tindakan
                                expandTindakan = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = diagnosa,
                onValueChange = { diagnosa = it },
                label = { Text("Diagnosa") },
                leadingIcon = { Icon(Icons.Default.Healing, null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = catatan,
                onValueChange = { catatan = it },
                label = { Text("Catatan") },
                leadingIcon = { Icon(Icons.Default.Note, null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            OutlinedTextField(
                value = resep,
                onValueChange = { resep = it },
                label = { Text("Resep Obat") },
                leadingIcon = { Icon(Icons.Default.Description, null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = idTindakan != null && diagnosa.isNotBlank(),
                onClick = {
                    val rm = RekamMedis(
                        id_rekam = rekamMedisId ?: 0,
                        id_janji = idJanji,
                        id_tindakan = idTindakan!!,
                        diagnosa = diagnosa,
                        catatan = catatan,
                        resep = resep
                    )

                    if (rekamMedisId == null)
                        viewModel.createRekamMedis(rm)
                    else
                        viewModel.updateRekamMedis(rm)
                }
            ) {
                Text(
                    text = if (rekamMedisId == null) "Simpan Data" else "Simpan Perubahan",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Sukses") },
            text = { Text("Data rekam medis berhasil disimpan!") },
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
