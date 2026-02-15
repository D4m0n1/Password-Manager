package com.d4m0n1.managerone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.*
import com.d4m0n1.managerone.data.datastore.SettingsDataStore
import com.d4m0n1.managerone.ui.navigation.AppNavigation
import com.d4m0n1.managerone.ui.theme.ManagerOneTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KoinAndroidContext {
                val settings: SettingsDataStore = koinInject()

                val isDark by settings.darkThemeFlow
                    .collectAsStateWithLifecycle(initialValue = false)

                ManagerOneTheme(darkTheme = isDark) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}
