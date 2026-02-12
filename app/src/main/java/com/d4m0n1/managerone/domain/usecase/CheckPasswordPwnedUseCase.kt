package com.d4m0n1.managerone.domain.usecase

import com.d4m0n1.managerone.data.remote.PwnedApi
import com.d4m0n1.managerone.domain.model.PwnedResult

class CheckPasswordPwnedUseCase(
    private val api: PwnedApi
) {
    suspend operator fun invoke(password: String): PwnedResult {
        if (password.isBlank()) return PwnedResult.Safe

        return try {
            val count = api.checkPassword(password)
                ?: return PwnedResult.Error("Сервер не ответил (таймаут)")

            when {
                count == 0 -> PwnedResult.Safe
                count > 0  -> PwnedResult.Pwned(count)
                else       -> PwnedResult.Safe
            }
        } catch (e: Exception) {
            when {
                e.message?.contains("timeout", ignoreCase = true) == true ->
                    PwnedResult.Error("Превышено время ожидания. Проверьте интернет.")
                else ->
                    PwnedResult.Error("Ошибка сети: ${e.localizedMessage ?: "Неизвестная ошибка"}")
            }
        }
    }
}