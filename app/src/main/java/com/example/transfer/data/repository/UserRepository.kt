package com.example.transfer.data.repository

import com.example.transfer.data.model.User

class UserRepository {
    suspend fun login(username: String, password: String): User? {
        // Simula la autenticación con un usuario estático
        val mockUser = User("1", "admin", "123", "admin")
        return if (username == mockUser.username && password == mockUser.password) {
            mockUser
        } else {
            null
        }
    }
}
