package com.example.klinikgigi.view

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
fun EntryRekamMedisScreen(
    viewModel: RekamMedisViewModel,
    rekamMedisId: Int?,
    idJanji: Int?,
    navigateBack: () -> Unit
) {

    LaunchedEffect(rekamMedisId) {
        rekamMedisId?.let { viewModel.loadRekamMedisById(it) }
        viewModel.loadJanjiTemu()
        viewModel.loadTindakan()
    }

    val rekamMedis by viewModel.selectedRekamMedis.collectAsState()
    val janjiList by viewModel.janjiList.collectAsState()
    val tindakanList by viewModel.tindakanList.collectAsState()

    var idJanji by remember { mutableStateOf<Int?>(null) }
    var idTindakan by remember { mutableStateOf<Int?>(null) }
    var diagnosa by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }
    var resep by remember { mutableStateOf("") }

    LaunchedEffect(rekamMedis) {
        rekamMedis?.let {
            idJanji = it.id_janji
            idTindakan = it.id_tindakan
            diagnosa = it.diagnosa
            catatan = it.catatan
            resep = it.resep
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (rekamMedisId == null) "Tambah Rekam Medis" else "Edit Rekam Medis") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ================= JANJI TEMU =================
            var expandJanji by remember { mutableStateOf(false) }
            val selectedJanji = janjiList.firstOrNull { it.id == idJanji }

            ExposedDropdownMenuBox(
                expanded = expandJanji,
                onExpandedChange = { expandJanji = !expandJanji }
            ) {
                OutlinedTextField(
                    value = selectedJanji?.let {
                        "ID ${it.id} | ${it.tanggal_janji} ${it.jam_janji}"
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Janji Temu") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expandJanji)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandJanji,
                    onDismissRequest = { expandJanji = false }
                ) {
                    janjiList.forEach { janji ->
                        DropdownMenuItem(
                            text = {
                                Text("ID ${janji.id} | ${janji.tanggal_janji} ${janji.jam_janji}")
                            },
                            onClick = {
                                idJanji = janji.id
                                expandJanji = false
                            }
                        )
                    }
                }
            }

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
                    label = { Text("Tindakan") },
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
                enabled = idJanji != null && idTindakan != null && diagnosa.isNotBlank(),
                onClick = {
                    val rm = RekamMedis(
                        id_rekam = rekamMedisId ?: 0,
                        id_janji = idJanji!!,
                        id_tindakan = idTindakan!!,
                        diagnosa = diagnosa,
                        catatan = catatan,
                        resep = resep
                    )

                    if (rekamMedisId == null)
                        viewModel.createRekamMedis(rm)
                    else
                        viewModel.updateRekamMedis(rm)

                    navigateBack()
                }
            ) {
                Text(if (rekamMedisId == null) "Simpan" else "Update")
            }
        }
    }
}
