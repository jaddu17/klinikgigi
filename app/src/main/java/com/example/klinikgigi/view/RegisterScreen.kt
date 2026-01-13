package com.example.klinikgigi.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.klinikgigi.viewmodel.AuthViewModel
import com.example.klinikgigi.uicontroller.route.DestinasiLogin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("dokter") }
    var expanded by remember { mutableStateOf(false) }

    val usernameValid = username.matches(Regex("^[a-zA-Z0-9_]*$"))

    val message by viewModel.message.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(message) {
        message?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)

                if (msg.contains("berhasil", ignoreCase = true)) {
                    delay(500)
                    navController.navigate(DestinasiLogin.route) {
                        popUpTo(DestinasiLogin.route) { inclusive = true }
                    }
                }
            }
            viewModel.clearMessage()
        }
    }

    val isFormValid = usernameValid && username.isNotBlank() && password.length >= 6

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.headlineMedium
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    isError = !usernameValid && username.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (!usernameValid && username.isNotBlank()) {
                    Text(
                        text = "Username hanya boleh huruf dan angka",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("admin", "dokter").forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    role = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (username.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Username wajib diisi")
                            }
                        } else if (!usernameValid) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Username tidak valid")
                            }
                        } else if (password.length < 6) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Password minimal 6 karakter")
                            }
                        } else {
                            viewModel.register(username, password, role)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Register")
                    }
                }

                TextButton(
                    onClick = {
                        navController.navigate(DestinasiLogin.route)
                    }
                ) {
                    Text("Sudah punya akun? Login")
                }
            }
        }
    }
}