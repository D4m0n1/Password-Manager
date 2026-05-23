package com.d4m0n1.managerone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4m0n1.managerone.domain.model.Password
import com.d4m0n1.managerone.domain.model.PwnedResult
import com.d4m0n1.managerone.domain.usecase.CheckPasswordPwnedUseCase
import com.d4m0n1.managerone.domain.usecase.DeletePasswordUseCase
import com.d4m0n1.managerone.domain.usecase.GetPasswordByIdUseCase
import com.d4m0n1.managerone.domain.usecase.UpdatePasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PasswordDetailViewModel(
    private val passwordId: Long,
    private val getByIdUseCase: GetPasswordByIdUseCase,
    private val updateUseCase: UpdatePasswordUseCase,
    private val deleteUseCase: DeletePasswordUseCase,
    private val checkPwnedUseCase: CheckPasswordPwnedUseCase
) : ViewModel() {

    // Основное состояние пароля
    private val _password = MutableStateFlow<Password?>(null)
    val password: StateFlow<Password?> = _password.asStateFlow()

    // Состояния для редактирования
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _serviceName = MutableStateFlow("")
    val serviceName: StateFlow<String> = _serviceName.asStateFlow()

    private val _login = MutableStateFlow("")
    val login: StateFlow<String> = _login.asStateFlow()

    private val _passValue = MutableStateFlow("")
    val passValue: StateFlow<String> = _passValue.asStateFlow()

    // Проверка на утечку
    private val _pwnedResult = MutableStateFlow<PwnedResult?>(null)
    val pwnedResult: StateFlow<PwnedResult?> = _pwnedResult.asStateFlow()

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> = _isChecking.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPassword()
    }

    private fun loadPassword() {
        viewModelScope.launch {
            getByIdUseCase(passwordId).collectLatest { p ->
                _password.value = p
                if (p != null) {
                    _serviceName.value = p.serviceName
                    _login.value = p.login
                    _passValue.value = p.password
                }
                _isLoading.value = false
            }
        }
    }

    fun onEditClick() {
        _isEditing.value = true
    }

    fun onServiceNameChange(newValue: String) {
        if (_isEditing.value) _serviceName.value = newValue
    }

    fun onLoginChange(newValue: String) {
        if (_isEditing.value) _login.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        if (_isEditing.value) {
            _passValue.value = newValue
            _pwnedResult.value = null // сбрасываем результат проверки
        }
    }


    fun checkPasswordStrength() {
        if (_passValue.value.isBlank()) return

        viewModelScope.launch {
            _isChecking.value = true
            _pwnedResult.value = checkPwnedUseCase(_passValue.value)
            _isChecking.value = false
        }
    }

    fun saveChanges() {
        val currentPassword = _password.value ?: return

        viewModelScope.launch {
            updateUseCase(
                currentPassword.copy(
                    serviceName = _serviceName.value.trim(),
                    login = _login.value.trim(),
                    password = _passValue.value.trim(),
                    updatedAt = System.currentTimeMillis()
                )
            )
            _isEditing.value = false
            _pwnedResult.value = null
        }
    }

    fun cancelEditing() {
        val current = _password.value
        if (current != null) {
            _serviceName.value = current.serviceName
            _login.value = current.login
            _passValue.value = current.password
        }
        _isEditing.value = false
        _pwnedResult.value = null
    }

    fun deletePassword() {
        viewModelScope.launch {
            deleteUseCase(passwordId)
        }
    }
}