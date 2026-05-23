package com.d4m0n1.managerone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4m0n1.managerone.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class PasswordListViewModel(
    repository: PasswordRepository
) : ViewModel() {

    val passwords = repository.getAllPasswords()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

}