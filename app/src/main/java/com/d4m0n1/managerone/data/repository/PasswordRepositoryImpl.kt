package com.d4m0n1.managerone.data.repository

import com.d4m0n1.managerone.data.local.PasswordDao
import com.d4m0n1.managerone.domain.model.Password
import com.d4m0n1.managerone.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow

class PasswordRepositoryImpl(
    private val dao: PasswordDao
) : PasswordRepository {    // реализует интерфейс

    override fun getAllPasswords(): Flow<List<Password>> = dao.getAllPasswords()

    override suspend fun addPassword(password: Password) {
        dao.insert(password)
    }

    override suspend fun deletePassword(id: Long) {
        dao.deleteById(id) // если id нет, то room ничего не сделает
    }

    override fun getPasswordById(id: Long): Flow<Password?> = dao.getPasswordById(id) // flow станет null, экран отреагирует

    override suspend fun updatePassword(password: Password) {
        dao.update(password)
    }
}