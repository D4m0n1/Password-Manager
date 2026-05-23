package com.d4m0n1.managerone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4m0n1.managerone.domain.model.Password
import com.d4m0n1.managerone.domain.repository.PasswordRepository
import kotlinx.coroutines.launch

class AddPasswordViewModel(
    private val repository: PasswordRepository
) : ViewModel() {

    // Стоит перенести сюда PwnedResoult и isChecking из экрана

    fun addPassword(password: Password) {
        viewModelScope.launch {
            repository.addPassword(password)
        }
    }

}