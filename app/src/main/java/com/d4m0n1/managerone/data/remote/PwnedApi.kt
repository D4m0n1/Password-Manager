package com.d4m0n1.managerone.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.http.*
import java.security.MessageDigest

class PwnedApi(
    private val client: HttpClient
) {
    private val baseUrl = "https://api.pwnedpasswords.com"

    suspend fun checkPassword(password: String): Int? {
        try {
            val sha1 = password.toSHA1().uppercase()
            val prefix = sha1.take(5)
            val suffix = sha1.drop(5)

            val responseText: String = client.get("$baseUrl/range/$prefix") {
                header(HttpHeaders.UserAgent, "PasswordManager/1.0 (Android)")
                header(HttpHeaders.Accept, ContentType.Text.Plain)
                // Можно задать таймаут конкретно для этого запроса
                timeout {
                    requestTimeoutMillis = 12000
                }
            }.body()

            val lines = responseText.lines()
            val match = lines.firstOrNull { it.startsWith(suffix, ignoreCase = true) }

            return match?.split(":")?.lastOrNull()?.toIntOrNull() ?: 0

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}

// Extension для SHA-1 (добавь в utils или в тот же файл)
fun String.toSHA1(): String {
    val bytes = MessageDigest.getInstance("SHA-1").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}