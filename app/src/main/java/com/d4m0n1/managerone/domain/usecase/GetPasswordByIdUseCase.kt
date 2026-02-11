package com.d4m0n1.managerone.domain.usecase

import com.d4m0n1.managerone.domain.model.Password
import com.d4m0n1.managerone.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow

class GetPasswordByIdUseCase(
    private val repository: PasswordRepository
) {
    operator fun invoke(id: Long): Flow<Password?> = repository.getPasswordById(id)
}