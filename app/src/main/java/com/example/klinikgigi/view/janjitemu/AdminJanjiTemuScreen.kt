package com.example.klinikgigi.view.janjitemu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.JanjiTemu
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
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedJanjiId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadJanji()
    }

    fun getPasienName(id: Int) = pasienList.find { it.id_pasien == id }?.nama_pasien ?: "Loading..."
    fun getDokterName(id: Int) = dokterList.find { it.id_dokter == id }?.nama_dokter ?: "Loading..."

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Janji Temu", fontWeight = FontWeight.Bold) },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Janji")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.searchJanji(it)
                },
                placeholder = { Text("Cari pasien / dokter...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (janjiList.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tidak ada data janji temu", color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(janjiList, key = { it.id_janji }) { janji ->
                            JanjiTemuCard(
                                janji = janji,
                                pasienName = getPasienName(janji.id_pasien),
                                dokterName = getDokterName(janji.id_dokter),
                                onEdit = { navigateToEdit(janji.id_janji) },
                                onDelete = {
                                    selectedJanjiId = janji.id_janji
                                    showDeleteDialog = true
                                },
                                onRekamMedis = {
                                    if (janji.sudah_ada_rekam_medis) {
                                        navigateToLihatRekamMedis(janji.id_janji)
                                    } else {
                                        navigateToEntryRekamMedis(janji.id_janji)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && selectedJanjiId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = { Text("Hapus Janji Temu") },
            text = { Text("Yakin ingin menghapus janji temu ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteJanji(selectedJanjiId!!)
                        showDeleteDialog = false
                        selectedJanjiId = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun JanjiTemuCard(
    janji: JanjiTemu,
    pasienName: String,
    dokterName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRekamMedis: () -> Unit
) {
    val statusColor = when (janji.status.name) {
        "MENUNGGU" -> MaterialTheme.colorScheme.tertiary
        "SELESAI" -> Color(0xFF4CAF50) // Green
        "BATAL" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    val statusBg = when (janji.status.name) {
        "MENUNGGU" -> MaterialTheme.colorScheme.tertiaryContainer
        "SELESAI" -> Color(0xFFE8F5E9) // Light Green
        "BATAL" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pasienName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "dr. $dokterName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg
                ) {
                    Text(
                        text = janji.status.name,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.surfaceVariant)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${janji.tanggal_janji} • ${janji.jam_janji}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (janji.keluhan.isNotBlank()) {
                Text(
                    text = "Keluhan: ${janji.keluhan}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (janji.status.name == "SELESAI") {
                    FilledTonalButton(
                        onClick = onRekamMedis,
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(if (janji.sudah_ada_rekam_medis) "Lihat Rekam Medis" else "Input Rekam Medis")
                    }
                } else {
                   Spacer(modifier = Modifier.width(1.dp)) // Spacer to push edit/delete to right if condition fails
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
