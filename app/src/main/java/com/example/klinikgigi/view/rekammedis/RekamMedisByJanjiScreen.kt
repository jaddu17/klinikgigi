package com.example.klinikgigi.view.rekammedis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.viewmodel.RekamMedisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekamMedisByJanjiScreen(
    idJanji: Int,
    viewModel: RekamMedisViewModel,
    navigateBack: () -> Unit,
    navigateToEdit: (Int) -> Unit
) {

    val rekamMedisList by viewModel.rekamMedisList.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.status.collectAsState()

    LaunchedEffect(idJanji) {
        viewModel.loadRekamMedisByJanji(idJanji)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rekam Medis") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->

        when {
            loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            rekamMedisList.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada rekam medis")
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(rekamMedisList, key = { it.id_rekam }) { rm ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = "Diagnosa",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(rm.diagnosa)

                            Divider()

                            Text(
                                text = "Catatan",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(rm.catatan)

                            Divider()

                            Text(
                                text = "Resep",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(rm.resep)

                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                TextButton(
                                    onClick = { navigateToEdit(rm.id_rekam) }
                                ) {
                                    Text("Edit")
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.deleteRekamMedis(rm.id_rekam)
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
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

        error?.let {
            LaunchedEffect(it) {
                viewModel.clearStatus()
            }
        }
    }
}
