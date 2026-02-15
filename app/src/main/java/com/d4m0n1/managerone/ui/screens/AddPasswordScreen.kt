package com.d4m0n1.managerone.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.d4m0n1.managerone.R
import com.d4m0n1.managerone.domain.model.Password
import com.d4m0n1.managerone.domain.model.PwnedResult
import com.d4m0n1.managerone.domain.repository.PasswordRepository
import com.d4m0n1.managerone.ui.viewmodel.AddPasswordViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.compose.runtime.rememberCoroutineScope
import com.d4m0n1.managerone.domain.usecase.CheckPasswordPwnedUseCase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPasswordScreen(
    navController: NavHostController,
    repository: PasswordRepository = koinInject(),
    viewModel: AddPasswordViewModel = koinViewModel()
) {
    var serviceName by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var serviceError by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    var pwnedResult by remember { mutableStateOf<PwnedResult?>(null) }
    var isChecking by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val checkPwnedUseCase: CheckPasswordPwnedUseCase = koinInject()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_password)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = serviceName,
                onValueChange = { serviceName = it },
                label = { Text(stringResource(R.string.service_name)) },
                isError = serviceError,
                supportingText = { if (serviceError) Text(stringResource(R.string.field_required)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                label = { Text(stringResource(R.string.login)) },
                isError = loginError,
                supportingText = { if (loginError) Text(stringResource(R.string.field_required)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (showPassword) "Скрыть" else "Показать"
                        )
                    }
                },
                supportingText = {
                    when (pwnedResult) {
                        is PwnedResult.Safe -> Text(
                            "Пароль выглядит безопасным",
                            color = MaterialTheme.colorScheme.primary
                        )

                        is PwnedResult.Pwned -> Text(
                            "Пароль утёк ${(pwnedResult as PwnedResult.Pwned).count} раз!",
                            color = MaterialTheme.colorScheme.error
                        )

                        is PwnedResult.Error -> Text(
                            (pwnedResult as PwnedResult.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )

                        null -> {}
                    }
                },
                isError = pwnedResult is PwnedResult.Pwned || pwnedResult is PwnedResult.Error,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        isChecking = true
                        pwnedResult = checkPwnedUseCase(password)
                        isChecking = false
                    }
                },
                enabled = !isChecking && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isChecking) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Проверить надёжность")
            }

            Button(
                onClick = {
                    serviceError = serviceName.isBlank()
                    loginError = login.isBlank()
                    passwordError = password.isBlank()

                    if (!serviceError && !loginError && !passwordError) {
                        viewModel.addPassword(
                            Password(
                                serviceName = serviceName.trim(),
                                login = login.trim(),
                                password = password.trim()
                            )
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }

        }
    }
}