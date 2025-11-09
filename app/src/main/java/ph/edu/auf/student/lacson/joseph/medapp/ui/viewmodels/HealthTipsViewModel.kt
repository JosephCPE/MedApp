package ph.edu.auf.student.lacson.joseph.medapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ph.edu.auf.student.lacson.joseph.medapp.data.local.entities.HealthTip
import ph.edu.auf.student.lacson.joseph.medapp.data.repository.MedAppRepository

class HealthTipsViewModel(private val repository: MedAppRepository) : ViewModel() {

    private val _healthTips = MutableStateFlow<List<HealthTip>>(emptyList())
    val healthTips: StateFlow<List<HealthTip>> = _healthTips.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadCachedTips()
    }

    private fun loadCachedTips() {
        viewModelScope.launch {
            repository.getHealthTipsFlow().collect { tips ->
                _healthTips.value = tips
            }
        }
    }

    fun fetchHealthTips() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.fetchHealthTipsFromApi()
            result.fold(
                onSuccess = {
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Failed to fetch health tips"
                    _isLoading.value = false
                }
            )
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}