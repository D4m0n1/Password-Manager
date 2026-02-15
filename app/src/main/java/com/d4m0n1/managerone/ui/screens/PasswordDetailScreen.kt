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
import androidx.navigation.NavHostController
import com.d4m0n1.managerone.domain.model.Password
import com.d4m0n1.managerone.domain.usecase.DeletePasswordUseCase
import com.d4m0n1.managerone.domain.usecase.GetPasswordByIdUseCase
import com.d4m0n1.managerone.domain.usecase.UpdatePasswordUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

import com.d4m0n1.managerone.domain.model.PwnedResult
import com.d4m0n1.managerone.domain.usecase.CheckPasswordPwnedUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordDetailScreen(
    navController: NavHostController,
    passwordId: Long,
    getById: GetPasswordByIdUseCase = koinInject(),
    updateUseCase: UpdatePasswordUseCase = koinInject(),
    deleteUseCase: DeletePasswordUseCase = koinInject(),
    checkPwnedUseCase: CheckPasswordPwnedUseCase = koinInject()
) {
    val scope = rememberCoroutineScope()
    var password by remember { mutableStateOf<Password?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showPassword by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    var serviceName by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var passValue by remember { mutableStateOf("") }

    // Новые состояния для проверки пароля
    var pwnedResult by remember { mutableStateOf<PwnedResult?>(null) }
    var isChecking by remember { mutableStateOf(false) }

    LaunchedEffect(passwordId) {
        getById(passwordId).collectLatest { p ->
            password = p
            if (p != null) {
                serviceName = p.serviceName
                login = p.login
                passValue = p.password
            }
            isLoading = false
        }
    }

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
                        IconButton(onClick = { isEditing = true }) {
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
                onValueChange = { if (isEditing) serviceName = it },
                label = { Text("Сервис") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = login,
                onValueChange = { if (isEditing) login = it },
                label = { Text("Логин") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth()
            )

            // Поле пароля с проверкой надёжности
            OutlinedTextField(
                value = passValue,
                onValueChange = {
                    if (isEditing) {
                        passValue = it
                        pwnedResult = null  // сбрасываем результат при изменении
                    }
                },
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

            // Кнопка проверки — показываем только в режиме редактирования
            if (isEditing) {
                Button(
                    onClick = {
                        scope.launch {
                            isChecking = true
                            pwnedResult = checkPwnedUseCase(passValue)
                            isChecking = false
                        }
                    },
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
                        onClick = {
                            scope.launch {
                                updateUseCase(
                                    password!!.copy(
                                        serviceName = serviceName.trim(),
                                        login = login.trim(),
                                        password = passValue.trim(),
                                        updatedAt = System.currentTimeMillis()
                                    )
                                )
                                isEditing = false
                                pwnedResult = null
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Сохранить") }

                    OutlinedButton(
                        onClick = {
                            isEditing = false
                            pwnedResult = null
                        },
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
                            scope.launch {
                                deleteUseCase(passwordId)
                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) { Text("Удалить") }
                }
            }
        }
    }
}