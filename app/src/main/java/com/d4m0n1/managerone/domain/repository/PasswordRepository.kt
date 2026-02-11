package com.d4m0n1.managerone.domain.repository

import com.d4m0n1.managerone.domain.model.Password
import kotlinx.coroutines.flow.Flow

interface PasswordRepository {
    fun getAllPasswords(): Flow<List<Password>>

    fun getPasswordById(id: Long): Flow<Password?>

    suspend fun addPassword(password: Password)

    suspend fun updatePassword(password: Password)

    suspend fun deletePassword(id: Long)
}