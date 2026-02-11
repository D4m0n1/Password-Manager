//package com.d4m0n1.managerone.data.repository
// В ПЕРСПЕКТИВЕ УДАЛИМ !!!
//import com.d4m0n1.managerone.domain.model.Password
//import com.d4m0n1.managerone.domain.repository.PasswordRepository
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
//class FakePasswordRepository : PasswordRepository {
//
//    private val _passwords = MutableStateFlow<List<Password>>(emptyList())
//
//    override fun getAllPasswords(): Flow<List<Password>> = _passwords.asStateFlow()
//
//    override suspend fun addPassword(password: Password) {
//        val current = _passwords.value.toMutableList()
//        val newId = if (current.isEmpty()) 1L else current.maxOf { it.id } + 1
//        current.add(password.copy(id = newId))
//        _passwords.value = current
//    }
//
//    override suspend fun deletePassword(id: Long) {
//        val current = _passwords.value.toMutableList()
//        current.removeAll { it.id == id }
//        _passwords.value = current
//    }
//}