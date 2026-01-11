package com.example.klinikgigi.repository

import android.app.Application
import com.example.klinikgigi.remote.ServiceApiKlinik
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

// KONTAINER UTAMA
interface ContainerApp {
    val repositoryKlinik: RepositoryKlinik
    val authRepository: AuthRepository
}

class DefaultContainerApp : ContainerApp {

    private val baseUrl = "http://10.0.2.2/api_klinik/"

    // Logging
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Retrofit
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true

                // ❗ WAJIB AGAR ENUM MENGIKUTI @SerialName
                encodeDefaults = true
                explicitNulls = false
            }.asConverterFactory("application/json".toMediaType())
        )
        .client(client)
        .build()


    private val apiKlinik: ServiceApiKlinik by lazy {
        retrofit.create(ServiceApiKlinik::class.java)
    }

    override val repositoryKlinik: RepositoryKlinik by lazy {
        JaringanRepositoryKlinik(apiKlinik)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepository(apiKlinik)
    }
}

// Init di Application
class AplikasiKlinik : Application() {

    lateinit var container: ContainerApp

    override fun onCreate() {
        super.onCreate()
        container = DefaultContainerApp()
    }
}
