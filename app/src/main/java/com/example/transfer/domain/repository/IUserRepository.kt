package com.example.transfer.domain.repository

import com.example.transfer.domain.model.UserDomain


interface IUserRepository {
    suspend fun login(username: String, password: String): UserDomain
}
