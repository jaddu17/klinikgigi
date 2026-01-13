package com.example.klinikgigi.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.klinikgigi.viewmodel.AuthViewModel
import com.example.klinikgigi.uicontroller.route.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val user by viewModel.user.collectAsState()
    val message by viewModel.message.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 🔑 State untuk error form (di bawah password)
    var loginError by remember { mutableStateOf<String?>(null) }

    // Reset error saat user mengetik ulang
    LaunchedEffect(username, password) {
        loginError = null
    }

    LaunchedEffect(user, message) {
        // Simpan ke variabel lokal
        val currentUser = user
        val currentMessage = message

        // ✅ Jika login SUKSES
        if (currentUser != null) {
            loginError = null
            val targetRoute = when (currentUser.role.lowercase()) {
                "admin" -> DestinasiAdminHome.route
                "dokter" -> DestinasiDokterHome.route
                else -> DestinasiLogin.route
            }
            navController.navigate(targetRoute) {
                popUpTo(0) { inclusive = true }
            }
            viewModel.clearMessage()
        }
        // ❌ Jika ada PESAN ERROR
        else if (currentMessage != null) {
            if (currentMessage == "Username dan password wajib diisi") {
                scope.launch {
                    snackbarHostState.showSnackbar(currentMessage)
                }
            } else {
                loginError = "Username atau password salah"
            }
            viewModel.clearMessage()
        }
    }

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
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineMedium
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    isError = loginError != null,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                // 👇 Tampilkan error hanya jika ada dan tidak loading
                if (!loading && loginError != null) {
                    Text(
                        text = loginError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp)
                            .fillMaxWidth()
                    )
                }

                Button(
                    onClick = { viewModel.login(username, password) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading && username.isNotBlank() && password.isNotBlank()
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Login")
                    }
                }

                TextButton(
                    onClick = { navController.navigate(DestinasiRegister.route) }
                ) {
                    Text("Belum punya akun? Register")
                }
            }
        }
    }
}