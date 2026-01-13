package com.example.klinikgigi.view.janjitemu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
    navigateToRekamMedis: (Int) -> Unit,
    navigateBack: () -> Unit
) {
    val janjiList by viewModel.janjiList.collectAsState()
    val pasienList by viewModel.pasienList.collectAsState()
    val dokterList by viewModel.dokterList.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var searchText by remember { mutableStateOf("") }

    fun namaPasien(id: Int) =
        pasienList.find { it.id_pasien == id }?.nama_pasien ?: "-"

    fun namaDokter(id: Int) =
        dokterList.find { it.id_dokter == id }?.nama_dokter ?: "-"

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
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // 🔍 SEARCH BAR (SRS)
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.searchJanji(it)
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Cari pasien / dokter / tanggal")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(janjiList, key = { it.id_janji }) { janji ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
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
                                Text("Status  : ${janji.status.name}")

                                Spacer(Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = { navigateToRekamMedis(janji.id_janji) }
                                    ) {
                                        Text("Rekam Medis")
                                    }

                                    Row {
                                        TextButton(
                                            onClick = { navigateToEdit(janji.id_janji) }
                                        ) {
                                            Text("Edit")
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        TextButton(
                                            onClick = { viewModel.deleteJanji(janji.id_janji) }
                                        ) {
                                            Text("Hapus")
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
}
