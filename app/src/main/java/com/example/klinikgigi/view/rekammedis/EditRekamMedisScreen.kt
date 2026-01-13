package com.example.klinikgigi.view.rekammedis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.RekamMedis
import com.example.klinikgigi.viewmodel.RekamMedisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRekamMedisScreen(
    viewModel: RekamMedisViewModel,
    rekamMedisId: Int,
    navigateBack: () -> Unit
) {

    val tindakanList by viewModel.tindakanList.collectAsState()
    val selectedRekamMedis by viewModel.selectedRekamMedis.collectAsState()
    val loading by viewModel.loading.collectAsState()

    // ================= FORM STATE =================
    var idJanji by remember { mutableStateOf("") }
    var idTindakan by remember { mutableStateOf<Int?>(null) }
    var diagnosa by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }
    var resep by remember { mutableStateOf("") }

    // ================= LOAD DATA =================
    LaunchedEffect(Unit) {
        viewModel.loadTindakan()
        viewModel.loadRekamMedisById(rekamMedisId)
    }

    LaunchedEffect(selectedRekamMedis) {
        selectedRekamMedis?.let {
            idJanji = it.id_janji.toString()
            idTindakan = it.id_tindakan
            diagnosa = it.diagnosa
            catatan = it.catatan
            resep = it.resep
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Rekam Medis") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->

        if (loading && selectedRekamMedis == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = idJanji,
                onValueChange = { idJanji = it },
                label = { Text("ID Janji") },
                modifier = Modifier.fillMaxWidth()
            )

            // ================= TINDAKAN =================
            var expandTindakan by remember { mutableStateOf(false) }
            val selectedTindakan = tindakanList.find { it.id_tindakan == idTindakan }

            ExposedDropdownMenuBox(
                expanded = expandTindakan,
                onExpandedChange = { expandTindakan = !expandTindakan }
            ) {
                OutlinedTextField(
                    value = selectedTindakan?.nama_tindakan ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tindakan") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandTindakan,
                    onDismissRequest = { expandTindakan = false }
                ) {
                    tindakanList.forEach {
                        DropdownMenuItem(
                            text = { Text(it.nama_tindakan) },
                            onClick = {
                                idTindakan = it.id_tindakan
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
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = catatan,
                onValueChange = { catatan = it },
                label = { Text("Catatan") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = resep,
                onValueChange = { resep = it },
                label = { Text("Resep") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = idJanji.isNotBlank() && idTindakan != null,
                onClick = {
                    val rm = RekamMedis(
                        id_rekam = rekamMedisId,
                        id_janji = idJanji.toInt(),
                        id_tindakan = idTindakan!!,
                        diagnosa = diagnosa,
                        catatan = catatan,
                        resep = resep
                    )
                    viewModel.updateRekamMedis(rm)
                    navigateBack()
                }
            ) {
                Text("Simpan Perubahan")
            }
        }
    }
}
