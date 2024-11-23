package com.example.transfer.domain.usecase

import com.example.transfer.domain.model.UserDomain
import com.example.transfer.domain.repository.IUserRepository

class LoginUseCase(private val userRepository: IUserRepository) {
    suspend operator fun invoke(username: String, password: String): UserDomain {
        return userRepository.login(username, password)
    }
}
