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

    // ================= HANDLE LOGIN RESULT =================
    LaunchedEffect(user, message) {

        // ❌ Jika gagal login
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }

        // ✅ Jika login sukses
        user?.let { loggedUser ->
            val targetRoute = when (loggedUser.role.lowercase()) {
                "admin" -> DestinasiAdminHome.route
                "dokter" -> DestinasiDokterHome.route
                else -> DestinasiLogin.route // fallback aman
            }

            navController.navigate(targetRoute) {
                // clear seluruh back stack supaya back button tidak kembali ke login
                popUpTo(0) { inclusive = true }
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.login(username, password) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading
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

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { navController.navigate(DestinasiRegister.route) }
                ) {
                    Text("Belum punya akun? Register")
                }
            }
        }
    }
}
