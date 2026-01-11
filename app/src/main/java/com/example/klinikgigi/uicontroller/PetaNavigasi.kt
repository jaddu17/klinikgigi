package com.example.klinikgigi.uicontroller

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.klinikgigi.uicontroller.route.*
import com.example.klinikgigi.view.*
import com.example.klinikgigi.view.dokter.DokterHomeScreen
import com.example.klinikgigi.view.route.DestinasiEditPasien
import com.example.klinikgigi.viewmodel.*
import com.example.klinikgigi.viewmodel.provider.PenyediaViewModel

@Composable
fun KlinikApp(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    HostNavigasiKlinik(navController = navController, modifier = modifier)
}

@Composable
fun HostNavigasiKlinik(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiLogin.route,
        modifier = modifier
    ) {

        // ================= LOGIN =================
        composable(DestinasiLogin.route) {
            val vm: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            LoginScreen(navController = navController, viewModel = vm)
        }

        // ================= REGISTER =================
        composable(DestinasiRegister.route) {
            val vm: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            RegisterScreen(navController = navController, viewModel = vm)
        }

        // ================= ADMIN HOME =================
        composable(DestinasiAdminHome.route) {
            val vm: DokterViewModel = viewModel(factory = PenyediaViewModel.Factory)
            AdminHomeScreen(
                dokterViewModel = vm,
                navigateToHalamanDokter = { navController.navigate(DestinasiDokter.route) },
                navigateToHalamanPasien = { navController.navigate(DestinasiHalamanPasien.route) },
                navigateToJanjiTemu = { navController.navigate(DestinasiAdminJanji.route) },
                navigateToHalamanTindakan = { navController.navigate(DestinasiTindakan.route) },
                navigateToHalamanRekamMedis = { navController.navigate(DestinasiRekamMedis.route) },
                navigateLogout = {
                    navController.navigate(DestinasiLogin.route) {
                        popUpTo(DestinasiLogin.route) { inclusive = true }
                    }
                }
            )
        }

        composable(DestinasiDokterHome.route) {
            DokterHomeScreen(
                navigateToJanjiTemu = {
                    navController.navigate(DestinasiDokterJanji.route)
                },
                navigateToRekamMedis = {
                    navController.navigate(DestinasiRekamMedisDokter.route)
                },
                navigateLogout = {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(DestinasiDokterJanji.route) {

            val viewModel: DokterDashboardViewModel =
                viewModel(factory = PenyediaViewModel.Factory)

            JanjiTemuDokterScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(DestinasiRekamMedisDokter.route) {

            val vm: RekamMedisViewModel =
                viewModel(factory = PenyediaViewModel.Factory)

            RekamMedisDokterScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }

        // ================= DOKTER =================
        composable(DestinasiDokter.route) {
            val vm: DokterViewModel = viewModel(factory = PenyediaViewModel.Factory)
            HalamanDokter(
                viewModel = vm,
                onTambah = { navController.navigate(DestinasiAddEditDokter.createRoute(null)) },
                onEdit = { id ->
                    navController.navigate("${DestinasiEditDokter.route}/$id")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DestinasiEditDokter.routeWithArgs,
            arguments = listOf(navArgument(DestinasiEditDokter.dokterIdArg) {
                type = NavType.IntType
            })
        ) {
            val id = it.arguments!!.getInt(DestinasiEditDokter.dokterIdArg)
            val vm: DokterViewModel = viewModel(factory = PenyediaViewModel.Factory)
            EditDokterScreen(dokterId = id, viewModel = vm) {
                navController.popBackStack()
            }
        }

        // ================= PASIEN =================
        composable(DestinasiHalamanPasien.route) {
            val vm: PasienViewModel = viewModel(factory = PenyediaViewModel.Factory)
            HalamanPasienScreen(
                pasienViewModel = vm,
                navigateToEntryPasien = { navController.navigate(DestinasiEntryPasien.route) },
                navigateToEditPasien = { id ->
                    navController.navigate("${DestinasiEditPasien.route}/$id")
                },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(DestinasiEntryPasien.route) {
            val vm: PasienViewModel = viewModel(factory = PenyediaViewModel.Factory)
            EntryPasienScreen(vm, null) { navController.popBackStack() }
        }

        composable(
            route = DestinasiEditPasien.routeWithArgs,
            arguments = listOf(navArgument(DestinasiEditPasien.pasienIdArg) {
                type = NavType.IntType
            })
        ) {
            val id = it.arguments!!.getInt(DestinasiEditPasien.pasienIdArg)
            val vm: PasienViewModel = viewModel(factory = PenyediaViewModel.Factory)
            EditPasienScreen(id, vm) { navController.popBackStack() }
        }

        // ================= JANJI TEMU =================
        composable(DestinasiAdminJanji.route) {
            val vm: JanjiTemuViewModel = viewModel(factory = PenyediaViewModel.Factory)
            AdminJanjiTemuScreen(
                viewModel = vm,
                navigateToAdd = { navController.navigate(DestinasiEntryJanji.route) },
                navigateToEdit = { id ->
                    navController.navigate("${DestinasiEditJanjiTemu.route}/$id")
                },
                navigateToRekamMedis = { idJanji ->
                    navController.navigate(
                        DestinasiEntryRekamMedis.createRoute(idJanji)
                    )
                },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(DestinasiEntryJanji.route) {
            val vm: JanjiTemuViewModel = viewModel(factory = PenyediaViewModel.Factory)
            EntryJanjiTemuScreen(vm, null) { navController.popBackStack() }
        }

        composable(
            route = DestinasiEditJanjiTemu.routeWithArgs,
            arguments = listOf(navArgument(DestinasiEditJanjiTemu.janjiIdArg) {
                type = NavType.IntType
            })
        ) {
            val id = it.arguments!!.getInt(DestinasiEditJanjiTemu.janjiIdArg)
            val vm: JanjiTemuViewModel = viewModel(factory = PenyediaViewModel.Factory)
            EditJanjiTemuScreen(vm, id) { navController.popBackStack() }
        }

        // ================= TINDAKAN =================
        composable(DestinasiTindakan.route) {
            val vm: TindakanViewModel = viewModel(factory = PenyediaViewModel.Factory)
            HalamanTindakanScreen(
                viewModel = vm,
                onTambah = { navController.navigate(DestinasiEntryTindakan.route) },
                onEdit = { id ->
                    navController.navigate("${DestinasiEditTindakan.route}/$id")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(DestinasiEntryTindakan.route) {
            val vm: TindakanViewModel = viewModel(factory = PenyediaViewModel.Factory)
            EntryTindakanScreen(vm) { navController.popBackStack() }
        }

        composable(
            route = DestinasiEditTindakan.routeWithArgs,
            arguments = listOf(navArgument(DestinasiEditTindakan.tindakanIdArg) {
                type = NavType.IntType
            })
        ) {
            val id = it.arguments!!.getInt(DestinasiEditTindakan.tindakanIdArg)
            val vm: TindakanViewModel = viewModel(factory = PenyediaViewModel.Factory)
            EditTindakanScreen(id, vm) { navController.popBackStack() }
        }

        // ================= REKAM MEDIS =================
        composable(DestinasiRekamMedis.route) {
            val vm: RekamMedisViewModel = viewModel(factory = PenyediaViewModel.Factory)
            HalamanRekamMedis(
                viewModel = vm,
                onEdit = { id ->
                    navController.navigate(DestinasiEditRekamMedis.createRoute(id))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DestinasiEntryRekamMedis.route,
            arguments = listOf(navArgument("idJanji") {
                type = NavType.IntType
            })
        ) {
            val idJanji = it.arguments!!.getInt("idJanji")
            val vm: RekamMedisViewModel = viewModel(factory = PenyediaViewModel.Factory)
            EntryRekamMedisScreen(vm, null, idJanji) {
                navController.popBackStack()
            }
        }

        composable(
            route = DestinasiEditRekamMedis.route,
            arguments = listOf(navArgument("idRekamMedis") {
                type = NavType.IntType
            })
        ) {
            val id = it.arguments!!.getInt("idRekamMedis")
            val vm: RekamMedisViewModel = viewModel(factory = PenyediaViewModel.Factory)
            EditRekamMedisScreen(vm, id) {
                navController.popBackStack()
            }
        }
    }
}
