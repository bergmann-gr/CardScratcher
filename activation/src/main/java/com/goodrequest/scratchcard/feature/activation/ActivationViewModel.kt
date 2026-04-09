package com.goodrequest.scratchcard.feature.activation

import androidx.lifecycle.ViewModel
import com.goodrequest.scratchcard.card.ScratchCardCoordinator
import com.goodrequest.scratchcard.activation.api.ActivationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

sealed interface ActivationUiState {
  data object Idle : ActivationUiState
  data object Loading : ActivationUiState
  data class Success(val androidVersion: Int) : ActivationUiState
  data class Error(val message: String) : ActivationUiState
}

@HiltViewModel
class ActivationViewModel @Inject constructor(
  private val coordinator: ScratchCardCoordinator,
) : ViewModel() {

  private val _uiState = MutableStateFlow<ActivationUiState>(ActivationUiState.Idle)
  val uiState: StateFlow<ActivationUiState> = _uiState.asStateFlow()

  fun activateCard() {
    if (_uiState.value is ActivationUiState.Loading) return

    _uiState.value = ActivationUiState.Loading
    viewModelScope.launch {
      when (val result = coordinator.activateCard()) {
        is ActivationResult.Activated -> _uiState.value = ActivationUiState.Success(result.androidVersion)
        is ActivationResult.Rejected -> _uiState.value =
          ActivationUiState.Error("Activation failed: ${result.androidVersion}")
        is ActivationResult.MissingCode -> _uiState.value =
          ActivationUiState.Error("No scratch code available for activation")
        is ActivationResult.Failure -> _uiState.value =
          ActivationUiState.Error(result.throwable.message ?: "Unknown error")
      }
    }
  }
}
