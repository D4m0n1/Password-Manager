package com.d4m0n1.managerone.di

import android.app.Application
import androidx.room.Room
import com.d4m0n1.managerone.data.local.AppDatabase
import com.d4m0n1.managerone.data.repository.PasswordRepositoryImpl
import com.d4m0n1.managerone.domain.repository.PasswordRepository
import com.d4m0n1.managerone.ui.viewmodel.AddPasswordViewModel
import com.d4m0n1.managerone.ui.viewmodel.PasswordListViewModel
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

    // ViewModels
    viewModelOf(::PasswordListViewModel)
    viewModelOf(::AddPasswordViewModel)
}