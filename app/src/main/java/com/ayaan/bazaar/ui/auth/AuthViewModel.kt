package com.ayaan.bazaar.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.bazaar.domain.model.User
import com.ayaan.bazaar.domain.repository.AuthRepository
import com.ayaan.bazaar.util.Result
import com.ayaan.bazaar.util.UiState
import com.ayaan.bazaar.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val uiState: StateFlow<UiState<User>> = _uiState.asStateFlow()

    private val _authState = MutableStateFlow<User?>(null)
    val authState: StateFlow<User?> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getAuthState().collect { user ->
                _authState.value = user
            }
        }
    }

    fun signUp(email: String, password: String, name: String) {
        // Validate inputs
        val emailError = Validators.validateEmail(email)
        val passwordError = Validators.validatePassword(password)
        val nameError = Validators.validateName(name)

        if (emailError != null) {
            _uiState.value = UiState.Error(emailError)
            return
        }
        if (passwordError != null) {
            _uiState.value = UiState.Error(passwordError)
            return
        }
        if (nameError != null) {
            _uiState.value = UiState.Error(nameError)
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = authRepository.signUp(email, password, name)) {
                is Result.Success -> {
                    _uiState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(result.exception.message ?: "Sign up failed")
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        // Validate inputs
        val emailError = Validators.validateEmail(email)
        val passwordError = Validators.validatePassword(password)

        if (emailError != null) {
            _uiState.value = UiState.Error(emailError)
            return
        }
        if (passwordError != null) {
            _uiState.value = UiState.Error(passwordError)
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = authRepository.signIn(email, password)) {
                is Result.Success -> {
                    _uiState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(result.exception.message ?: "Sign in failed")
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun clearError() {
        _uiState.value = UiState.Idle
    }
}
