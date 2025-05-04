package com.example.travellelotralala.ui.screens.authscreens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.User
import com.example.travellelotralala.repository.AuthRepository
import com.example.travellelotralala.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _signupState = MutableStateFlow<SignupState>(SignupState.Initial)
    val signupState: StateFlow<SignupState> = _signupState
    
    fun signup(email: String, password: String, name: String) {
        if (!validateInput(email, password, name)) {
            return
        }
        
        viewModelScope.launch {
            _signupState.value = SignupState.Loading
            
            authRepository.signUp(email, password, name)
                .onSuccess { user ->
                    _signupState.value = SignupState.Success(user)
                }
                .onFailure { exception ->
                    _signupState.value = SignupState.Error(
                        exception.message ?: "Unknown error occurred"
                    )
                }
        }
    }
    
    private fun validateInput(email: String, password: String, name: String): Boolean {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _signupState.value = SignupState.Error("All fields are required")
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _signupState.value = SignupState.Error("Invalid email format")
            return false
        }
        
        if (password.length < 6) {
            _signupState.value = SignupState.Error("Password must be at least 6 characters")
            return false
        }
        
        return true
    }
}

sealed class SignupState {
    object Initial : SignupState()
    object Loading : SignupState()
    data class Success(val user: User) : SignupState()
    data class Error(val message: String) : SignupState()
}
