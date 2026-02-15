package com.d4m0n1.managerone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4m0n1.managerone.domain.model.Password
import com.d4m0n1.managerone.domain.repository.PasswordRepository
import com.d4m0n1.managerone.ui.state.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PasswordListViewModel(
    private val repository: PasswordRepository
) : ViewModel() {

    val passwords = repository.getAllPasswords()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val uiState: StateFlow<UiState<List<Password>>> = repository.getAllPasswords()
        .map { list ->
            when {
                list.isEmpty() -> UiState.Empty
                else -> UiState.Success(list)
            }
        }
        .catch { UiState.Error(it.localizedMessage ?: "Ошибка") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

}