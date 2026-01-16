package com.example.klinikgigi.view.rekammedis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.RekamMedis
import com.example.klinikgigi.viewmodel.RekamMedisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanRekamMedis(
    viewModel: RekamMedisViewModel,
    isAdmin: Boolean = true,
    onEdit: (Int) -> Unit,
    onBack: () -> Unit
) {
    val rekamMedisList by viewModel.rekamMedisList.collectAsState()
    val janjiList by viewModel.janjiList.collectAsState()
    val dokterList by viewModel.dokterList.collectAsState()
    val pasienList by viewModel.pasienList.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var rekamMedisHapus by remember { mutableStateOf<RekamMedis?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadRekamMedis()
        viewModel.loadJanjiTemu()
        viewModel.loadDokter()
        viewModel.loadPasien()
    }

    // Helper function to get names
    fun getDetails(idJanji: Int): Pair<String, String> {
        val janji = janjiList.find { it.id_janji == idJanji }
        val dokter = dokterList.find { it.id_dokter == janji?.id_dokter }
        val pasien = pasienList.find { it.id_pasien == janji?.id_pasien }
        return Pair(dokter?.nama_dokter ?: "-", pasien?.nama_pasien ?: "-")
    }

    // Filter Logic
    val filteredList = rekamMedisList.filter { rm ->
        val (dokterName, pasienName) = getDetails(rm.id_janji)
        val query = searchQuery.lowercase()
        dokterName.lowercase().contains(query) ||
        pasienName.lowercase().contains(query) ||
        rm.diagnosa.lowercase().contains(query)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rekam Medis", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        containerColor = Color(0xFFF5F5F5) // Light Grey Background to contrast with White Cards
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            
            // 🔍 Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Cari Pasien, Dokter, atau Diagnosa...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            if(searchQuery.isBlank()) "Belum ada data rekam medis" else "Data tidak ditemukan", 
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { rm ->
                        val (dokterName, pasienName) = getDetails(rm.id_janji)

                        RekamMedisCard(
                            rekamMedis = rm,
                            dokterName = dokterName,
                            pasienName = pasienName,
                            isAdmin = isAdmin,
                            onEdit = { onEdit(rm.id_rekam) },
                            onDelete = { rekamMedisHapus = rm }
                        )
                    }
                }
            }
        }
    }

    // ================= KONFIRMASI HAPUS =================
    rekamMedisHapus?.let { rm ->
        AlertDialog(
            onDismissRequest = { rekamMedisHapus = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = { Text("Hapus Rekam Medis") },
            text = {
                Text("Yakin ingin menghapus rekam medis ini?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRekamMedis(rm.id_rekam)
                        rekamMedisHapus = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { rekamMedisHapus = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun RekamMedisCard(
    rekamMedis: RekamMedis,
    dokterName: String,
    pasienName: String,
    isAdmin: Boolean = true,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // White Card
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with Names
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Pasien: $pasienName",
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
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "ID: ${rekamMedis.id_rekam}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)

            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.tertiary)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Diagnosa",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = rekamMedis.diagnosa,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (rekamMedis.catatan.isNotBlank()) {
                Row(verticalAlignment = Alignment.Top) {
                   Icon(Icons.Default.Note, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.outline)
                   Spacer(Modifier.width(8.dp))
                   Text("Catatan: ${rekamMedis.catatan}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.surfaceVariant)

            if (isAdmin) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Edit")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Hapus")
                    }
                }
            }
        }
    }
}
