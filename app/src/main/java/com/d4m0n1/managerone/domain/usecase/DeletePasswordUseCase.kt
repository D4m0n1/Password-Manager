package com.d4m0n1.managerone.domain.usecase

import com.d4m0n1.managerone.domain.repository.PasswordRepository

class DeletePasswordUseCase(
    private val repository: PasswordRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deletePassword(id)
    }
}