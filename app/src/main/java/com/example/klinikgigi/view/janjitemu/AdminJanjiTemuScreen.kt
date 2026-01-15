package com.example.klinikgigi.view.janjitemu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.viewmodel.JanjiTemuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminJanjiTemuScreen(
    viewModel: JanjiTemuViewModel,
    navigateToAdd: () -> Unit,
    navigateToEdit: (Int) -> Unit,
    navigateToEntryRekamMedis: (Int) -> Unit,
    navigateToLihatRekamMedis: (Int) -> Unit,
    navigateBack: () -> Unit
) {

    val janjiList by viewModel.janjiList.collectAsState()
    val pasienList by viewModel.pasienList.collectAsState()
    val dokterList by viewModel.dokterList.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var searchText by remember { mutableStateOf("") }

    /** 🔥 STATE HAPUS */
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedJanjiId by remember { mutableStateOf<Int?>(null) }

    fun namaPasien(id: Int) =
        pasienList.find { it.id_pasien == id }?.nama_pasien ?: "-"

    fun namaDokter(id: Int) =
        dokterList.find { it.id_dokter == id }?.nama_dokter ?: "-"

    fun statusLabel(status: String) = when (status.lowercase()) {
        "menunggu" -> "Menunggu"
        "selesai" -> "Selesai"
        "batal" -> "Batal"
        else -> status
    }

    LaunchedEffect(Unit) {
        viewModel.loadJanji()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Janji Temu") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Janji Temu")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.searchJanji(it)
                },
                label = { Text("Cari pasien / dokter / tanggal") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            when {
                loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                janjiList.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada data janji temu")
                }

                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(janjiList, key = { it.id_janji }) { janji ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {

                                Text(
                                    text = namaPasien(janji.id_pasien),
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text("Dokter  : ${namaDokter(janji.id_dokter)}")
                                Text("Tanggal : ${janji.tanggal_janji} • ${janji.jam_janji}")
                                Text("Keluhan : ${janji.keluhan}")
                                Text("Status  : ${statusLabel(janji.status.name)}")

                                Spacer(Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    /** ================= KIRI ================= */
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (janji.status.name.lowercase() == "selesai") {
                                            AssistChip(
                                                onClick = {
                                                    if (janji.sudah_ada_rekam_medis) {
                                                        navigateToLihatRekamMedis(janji.id_janji)
                                                    } else {
                                                        navigateToEntryRekamMedis(janji.id_janji)
                                                    }
                                                },
                                                label = {
                                                    Text(
                                                        if (janji.sudah_ada_rekam_medis)
                                                            "Lihat Rekam Medis"
                                                        else
                                                            "Tambah Rekam Medis"
                                                    )
                                                }
                                            )
                                        } else {
                                            Text(
                                                text = "Rekam medis hanya tersedia setelah janji selesai",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    /** ================= KANAN ================= */
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        TextButton(
                                            onClick = { navigateToEdit(janji.id_janji) }
                                        ) {
                                            Text("Edit")
                                        }

                                        IconButton(
                                            onClick = {
                                                selectedJanjiId = janji.id_janji
                                                showDeleteDialog = true
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Hapus",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /** ================= DIALOG HAPUS ================= */
    if (showDeleteDialog && selectedJanjiId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Janji Temu") },
            text = { Text("Apakah Anda yakin ingin menghapus janji temu ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteJanji(selectedJanjiId!!)
                        showDeleteDialog = false
                        selectedJanjiId = null
                    }
                ) {
                    Text(
                        text = "Ya, Hapus",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        selectedJanjiId = null
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}
