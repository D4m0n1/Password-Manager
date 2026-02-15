package com.d4m0n1.managerone.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.d4m0n1.managerone.data.datastore.SettingsDataStore
import com.d4m0n1.managerone.data.local.AppDatabase
import com.d4m0n1.managerone.data.remote.PwnedApi
import com.d4m0n1.managerone.data.repository.PasswordRepositoryImpl
import com.d4m0n1.managerone.domain.repository.PasswordRepository
import com.d4m0n1.managerone.domain.usecase.CheckPasswordPwnedUseCase
import com.d4m0n1.managerone.domain.usecase.DeletePasswordUseCase
import com.d4m0n1.managerone.domain.usecase.GetPasswordByIdUseCase
import com.d4m0n1.managerone.domain.usecase.UpdatePasswordUseCase
import com.d4m0n1.managerone.ui.viewmodel.AddPasswordViewModel
import com.d4m0n1.managerone.ui.viewmodel.PasswordListViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    // Database
    single<AppDatabase> {
        Room.databaseBuilder(
            get<Application>(),
            AppDatabase::class.java,
            "password_manager.db"
        ).build()
    }

    // DAO
    single { get<AppDatabase>().passwordDao() }

    // Repository
    single<PasswordRepository> { PasswordRepositoryImpl(get()) }

    // PwnedService
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000   // общий таймаут запроса
                connectTimeoutMillis = 8000    // таймаут подключения
                socketTimeoutMillis = 15000    // таймаут чтения данных
            }
        }
    }
    single<PwnedApi> { PwnedApi(get()) }

    // Theme
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create {
            get<Application>().applicationContext.preferencesDataStoreFile("settings")
        }
    }
    single { SettingsDataStore(get()) }

    // Use Cases
    factory { CheckPasswordPwnedUseCase(get()) } // <- этот для PwnedService
    factory { GetPasswordByIdUseCase(get()) }
    factory { UpdatePasswordUseCase(get()) }
    factory { DeletePasswordUseCase(get()) }

    // ViewModels
    viewModelOf(::PasswordListViewModel)
    viewModelOf(::AddPasswordViewModel)
}