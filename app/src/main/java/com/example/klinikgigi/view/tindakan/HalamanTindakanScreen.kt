package com.example.klinikgigi.view.tindakan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.modeldata.Tindakan
import com.example.klinikgigi.viewmodel.TindakanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTindakanScreen(
    viewModel: TindakanViewModel,
    onTambah: () -> Unit,
    onEdit: (Int) -> Unit,
    onBack: () -> Unit
) {
    val tindakanList by viewModel.tindakanList.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var tindakanYangAkanDihapus by remember { mutableStateOf<Tindakan?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadTindakan()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Tindakan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = onTambah) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Tindakan")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {

            // 🔍 SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cari tindakan...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {

                if (tindakanList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Data tindakan tidak ditemukan", color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(tindakanList) { tindakan ->
                            tindakan.id_tindakan?.let { id ->
                                TindakanItem(
                                    tindakan = tindakan,
                                    onEdit = { onEdit(id) },
                                    onDelete = { tindakanYangAkanDihapus = tindakan }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 🗑️ KONFIRMASI HAPUS
    tindakanYangAkanDihapus?.let { tindakan ->
        AlertDialog(
            onDismissRequest = { tindakanYangAkanDihapus = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = { Text("Hapus Tindakan") },
            text = { Text("Yakin ingin menghapus tindakan \"${tindakan.nama_tindakan}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        tindakan.id_tindakan?.let { viewModel.deleteTindakan(it) }
                        tindakanYangAkanDihapus = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { tindakanYangAkanDihapus = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun TindakanItem(
    tindakan: Tindakan,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Icon Placeholder
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.MedicalServices, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tindakan.nama_tindakan,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Rp ${tindakan.harga}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
                if (tindakan.deskripsi.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tindakan.deskripsi,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            // Actions (Vertical to save horizontal space)
            Column {
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
