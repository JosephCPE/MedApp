package ph.edu.auf.student.lacson.joseph.medapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ph.edu.auf.student.lacson.joseph.medapp.data.local.entities.HealthLog
import ph.edu.auf.student.lacson.joseph.medapp.data.repository.MedAppRepository

class HealthLogViewModel(private val repository: MedAppRepository) : ViewModel() {

    private val _healthLogs = MutableStateFlow<List<HealthLog>>(emptyList())
    val healthLogs: StateFlow<List<HealthLog>> = _healthLogs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun loadHealthLogs() {
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.getHealthLogsFlow(userId).collect { logs ->
                _healthLogs.value = logs
                _isLoading.value = false
            }
        }
    }

    fun saveHealthLog(
        date: Long,
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        temperature: Double,
        weight: Double
    ) {
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _isSaving.value = true
            val log = HealthLog(
                userId = userId,
                date = date,
                systolic = systolic,
                diastolic = diastolic,
                heartRate = heartRate,
                temperature = temperature,
                weight = weight,
                timestamp = System.currentTimeMillis()
            )
            repository.saveHealthLog(log)
            _isSaving.value = false
        }
    }
}