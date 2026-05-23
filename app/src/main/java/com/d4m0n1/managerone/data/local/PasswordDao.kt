package com.d4m0n1.managerone.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.d4m0n1.managerone.domain.model.Password
import kotlinx.coroutines.flow.Flow

@Dao // интерфейс с методами доступа к базе данных
interface PasswordDao {

    @Query("SELECT * FROM passwords ORDER BY serviceName ASC") // взять записи passwords, отсортировать по полю serviceName по возрастанию
    fun getAllPasswords(): Flow<List<Password>> // будет автоматически обновляться

    @Insert
    suspend fun insert(password: Password) // suspend может вызваться только внутри корутин

    @Update
    suspend fun update(password: Password)

    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM passwords WHERE id = :id LIMIT 1") // limit - оптимизация
    fun getPasswordById(id: Long): Flow<Password?>
}