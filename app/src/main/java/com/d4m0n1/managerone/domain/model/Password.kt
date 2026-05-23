package com.d4m0n1.managerone.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Единственная сущность на весь проект, поэтому можно оставить в domain

@Entity(tableName = "passwords") // этот класс должен стать таблицей в БД
data class Password(
    @PrimaryKey(autoGenerate = true) // применяется к следующему полю id, сделав ключом
    val id: Long = 0, // Помимо удобства, передача именно id защищает от sql-инъекций
    val serviceName: String,
    val login: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)