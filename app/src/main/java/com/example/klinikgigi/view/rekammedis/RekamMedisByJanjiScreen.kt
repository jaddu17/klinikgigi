package com.example.klinikgigi.view.rekammedis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.klinikgigi.viewmodel.RekamMedisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekamMedisByJanjiScreen(
    idJanji: Int,
    viewModel: RekamMedisViewModel,
    isAdmin: Boolean = true, // Default true agar pemanggil lama tidak error
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
                title = { 
                    Text(
                        if (isAdmin) "Detail Rekam Medis" else "Detail Rekam Medis (View Only)", 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
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
                Text("Belum ada rekam medis untuk janji ini", color = MaterialTheme.colorScheme.outline)
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(rekamMedisList, key = { it.id_rekam }) { rm ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            
                            InfoSection(
                                icon = Icons.Default.Healing,
                                title = "Diagnosa",
                                content = rm.diagnosa
                            )
                            
                            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                            
                            InfoSection(
                                icon = Icons.Default.Note,
                                title = "Catatan",
                                content = rm.catatan.ifBlank { "-" }
                            )

                            Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                            InfoSection(
                                icon = Icons.Default.Description,
                                title = "Resep Obat",
                                content = rm.resep.ifBlank { "-" }
                            )

                            if (isAdmin) {
                                Spacer(Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { navigateToEdit(rm.id_rekam) },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text("Edit")
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    Button(
                                        onClick = { viewModel.deleteRekamMedis(rm.id_rekam) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer,
                                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text("Hapus")
                                    }
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

@Composable
fun InfoSection(icon: ImageVector, title: String, content: String) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
