package ph.edu.auf.student.lacson.joseph.medapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ph.edu.auf.student.lacson.joseph.medapp.data.local.entities.UserProfile
import ph.edu.auf.student.lacson.joseph.medapp.data.repository.MedAppRepository

class ProfileViewModel(private val repository: MedAppRepository) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun loadProfile() {
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.getUserProfileFlow(userId).collect { profile ->
                _profile.value = profile
                _isLoading.value = false
            }
        }
    }

    fun saveProfile(name: String, age: Int, weight: Double, healthConditions: String) {
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _isSaving.value = true
            val profile = UserProfile(
                userId = userId,
                name = name,
                age = age,
                weight = weight,
                healthConditions = healthConditions,
                lastSyncTimestamp = System.currentTimeMillis()
            )
            repository.saveUserProfile(profile)
            _isSaving.value = false
        }
    }

    fun syncFromCloud() {
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.syncFromFirestore(userId)
            _isLoading.value = false
        }
    }
}