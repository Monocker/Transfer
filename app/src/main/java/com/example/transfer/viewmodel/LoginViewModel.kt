package com.example.transfer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transfer.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginState: StateFlow<LoginResult> = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val result = loginUseCase(username, password)
            _loginState.value = if (result != null) LoginResult.Success else LoginResult.Error
        }
    }
}

sealed class LoginResult {
    object Idle : LoginResult()
    object Success : LoginResult()
    object Error : LoginResult()
}