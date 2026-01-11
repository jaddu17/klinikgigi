package com.example.klinikgigi.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.klinikgigi.repository.AplikasiKlinik
import com.example.klinikgigi.viewmodel.AuthViewModel
import com.example.klinikgigi.viewmodel.DokterViewModel
import com.example.klinikgigi.viewmodel.DokterDashboardViewModel
import com.example.klinikgigi.viewmodel.JanjiTemuViewModel
import com.example.klinikgigi.viewmodel.PasienViewModel
import com.example.klinikgigi.viewmodel.RekamMedisViewModel
import com.example.klinikgigi.viewmodel.TindakanViewModel

// Ambil Application (AplikasiKlinik)
fun CreationExtras.aplikasiKlinik(): AplikasiKlinik =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AplikasiKlinik)

// Provider ViewModel (Sama template Tugas)
object PenyediaViewModel {
    val Factory = viewModelFactory {

        // ----- ViewModel untuk daftar dokter -----
        initializer {
            DokterViewModel(
                aplikasiKlinik().container.repositoryKlinik
            )
        }

        initializer {
            AuthViewModel(
                aplikasiKlinik().container.authRepository
            )
        }

        initializer {
            JanjiTemuViewModel(
                aplikasiKlinik().container.repositoryKlinik
            )
        }

        initializer {
            PasienViewModel(
                aplikasiKlinik().container.repositoryKlinik
            )
        }
        initializer {
            TindakanViewModel(aplikasiKlinik().container.repositoryKlinik
            )
        }
        initializer {
            RekamMedisViewModel(
                aplikasiKlinik().container.repositoryKlinik
            )
        }
        initializer {
            DokterDashboardViewModel(
                aplikasiKlinik().container.repositoryKlinik
            )
        }
    }
}

