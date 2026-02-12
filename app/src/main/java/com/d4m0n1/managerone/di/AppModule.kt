package com.d4m0n1.managerone.di

import android.app.Application
import androidx.room.Room
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
                requestTimeoutMillis = 15000   // общий таймаут запроса (15 сек)
                connectTimeoutMillis = 8000    // таймаут подключения
                socketTimeoutMillis = 15000    // таймаут чтения данных
            }
        }
    }
    single<PwnedApi> { PwnedApi(get()) }
    factory { CheckPasswordPwnedUseCase(get()) }

    // Use Cases
    factory { GetPasswordByIdUseCase(get()) }
    factory { UpdatePasswordUseCase(get()) }
    factory { DeletePasswordUseCase(get()) }

    // ViewModels
    viewModelOf(::PasswordListViewModel)
    viewModelOf(::AddPasswordViewModel)
}