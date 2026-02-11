package com.d4m0n1.managerone.domain.usecase

import com.d4m0n1.managerone.domain.model.Password
import com.d4m0n1.managerone.domain.repository.PasswordRepository

class UpdatePasswordUseCase(
    private val repository: PasswordRepository
) {
    suspend operator fun invoke(password: Password) {
        repository.updatePassword(password)
    }
}