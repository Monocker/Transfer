package com.example.transfer.domain.usecase

import com.example.transfer.data.model.User
import com.example.transfer.data.repository.UserRepository
import com.example.transfer.domain.model.UserDomain

class LoginUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(username: String, password: String): User? {
        return userRepository.login(username, password)
    }
}
