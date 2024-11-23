package com.example.transfer.data.repository

import com.example.transfer.data.model.User
import com.example.transfer.data.source.remote.AuthService

//mplementa la lógica para obtener y almacenar datos del usuario, actuando como intermediario entre las fuentes de datos y el resto de la aplicación.

class UserRepository {
    suspend fun login(username: String, password: String): User? {
        // Datos estáticos para simular la autenticación
        val mockUser = User("1", "admin", "123", "admin")
        return if (username == mockUser.username && password == mockUser.password) {
            mockUser
        } else {
            null
        }
    }
}

