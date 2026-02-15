package com.d4m0n1.managerone

import android.app.Application
import com.d4m0n1.managerone.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PasswordManagerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@PasswordManagerApplication)
            modules(appModule)
        }
    }
}