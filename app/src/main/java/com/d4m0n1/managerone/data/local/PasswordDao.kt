package com.d4m0n1.managerone.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.d4m0n1.managerone.domain.model.Password
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {

    @Query("SELECT * FROM passwords ORDER BY serviceName ASC")
    fun getAllPasswords(): Flow<List<Password>>

    @Insert
    suspend fun insert(password: Password)

    @Update
    suspend fun update(password: Password)

    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun deleteById(id: Long)
}