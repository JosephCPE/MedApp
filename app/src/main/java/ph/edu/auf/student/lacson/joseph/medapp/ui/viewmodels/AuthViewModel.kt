package ph.edu.auf.student.lacson.joseph.medapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ph.edu.auf.student.lacson.joseph.medapp.data.repository.MedAppRepository

class AuthViewModel(private val repository: MedAppRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        data class Success(val userId: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    fun signUpWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _isLoading.value = true
            val result = repository.signUpWithEmail(email, password)
            result.fold(
                onSuccess = { userId ->
                    _authState.value = AuthState.Success(userId)
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Unknown error")
                    _isLoading.value = false
                }
            )
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _isLoading.value = true
            val result = repository.signInWithEmail(email, password)
            result.fold(
                onSuccess = { userId ->
                    _authState.value = AuthState.Success(userId)
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Unknown error")
                    _isLoading.value = false
                }
            )
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _isLoading.value = true
            val result = repository.signInWithGoogle(idToken)
            result.fold(
                onSuccess = { userId ->
                    _authState.value = AuthState.Success(userId)
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Unknown error")
                    _isLoading.value = false
                }
            )
        }
    }

    fun signOut() {
        repository.signOut()
        _authState.value = AuthState.Initial
    }

    fun resetAuthState() {
        _authState.value = AuthState.Initial
    }
}