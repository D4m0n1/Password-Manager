package com.d4m0n1.managerone.domain.model

sealed class PwnedResult {
    data object Safe : PwnedResult()                        // 0 утечек
    data class Pwned(val count: Int) : PwnedResult()        // >0 утечек
    data class Error(val message: String = "Ошибка сети") : PwnedResult()
}