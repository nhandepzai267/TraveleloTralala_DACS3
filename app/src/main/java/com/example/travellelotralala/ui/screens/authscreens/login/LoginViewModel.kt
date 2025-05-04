package com.example.travellelotralala.ui.screens.authscreens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.User
import com.example.travellelotralala.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState
    
    fun login(email: String, password: String) {
        if (!validateInput(email, password)) {
            return
        }
        
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            authRepository.signIn(email, password)
                .onSuccess { user ->
                    _loginState.value = LoginState.Success(user)
                }
                .onFailure { exception ->
                    _loginState.value = LoginState.Error(
                        exception.message ?: "Unknown error occurred"
                    )
                }
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Email and password are required")
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginState.value = LoginState.Error("Invalid email format")
            return false
        }
        
        return true
    }
}

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
