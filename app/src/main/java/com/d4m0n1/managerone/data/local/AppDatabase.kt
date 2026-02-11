package com.d4m0n1.managerone.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.d4m0n1.managerone.domain.model.Password

@Database(
    entities = [Password::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun passwordDao(): PasswordDao
}