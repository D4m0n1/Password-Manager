package com.d4m0n1.managerone.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.d4m0n1.managerone.domain.model.PwnedResult
import com.d4m0n1.managerone.ui.viewmodel.PasswordDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordDetailScreen(
    navController: NavHostController,
    passwordId: Long,
    viewModel: PasswordDetailViewModel = koinViewModel { parametersOf(passwordId) }
) {
    val password by viewModel.password.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()

    val serviceName by viewModel.serviceName.collectAsStateWithLifecycle()
    val login by viewModel.login.collectAsStateWithLifecycle()
    val passValue by viewModel.passValue.collectAsStateWithLifecycle()
    val pwnedResult by viewModel.pwnedResult.collectAsStateWithLifecycle()
    val isChecking by viewModel.isChecking.collectAsStateWithLifecycle()

    var showPassword by remember { mutableStateOf(false) } // чисто UI-состояние

    val clipboard = LocalClipboardManager.current

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (password == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Пароль не найден")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Редактирование" else "Детали пароля") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = { viewModel.onEditClick() }) {
                            Icon(Icons.Default.Edit, "Редактировать")
                        }
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
                onValueChange = { viewModel.onServiceNameChange(it) },
                label = { Text("Сервис") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = login,
                onValueChange = { viewModel.onLoginChange(it) },
                label = { Text("Логин") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = passValue,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Пароль") },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
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
                readOnly = !isEditing,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            if (isEditing) {
                Button(
                    onClick = { viewModel.checkPasswordStrength() },
                    enabled = !isChecking && passValue.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isChecking) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Проверить надёжность")
                    }
                }
            }

            if (isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.saveChanges() },
                        modifier = Modifier.weight(1f)
                    ) { Text("Сохранить") }

                    OutlinedButton(
                        onClick = { viewModel.cancelEditing() },
                        modifier = Modifier.weight(1f)
                    ) { Text("Отмена") }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { clipboard.setText(AnnotatedString(passValue)) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Копировать пароль") }

                    Button(
                        onClick = {
                            viewModel.deletePassword()
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) { Text("Удалить") }
                }
            }
        }
    }
}