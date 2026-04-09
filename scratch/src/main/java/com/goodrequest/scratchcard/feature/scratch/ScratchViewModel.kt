package com.goodrequest.scratchcard.feature.scratch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodrequest.scratchcard.card.CardRepository
import com.goodrequest.scratchcard.card.ScratchCardCoordinator
import com.goodrequest.scratchcard.card.model.CardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ScratchUiState {
  data object Idle : ScratchUiState
  data object Loading : ScratchUiState
  data class Success(val code: String) : ScratchUiState
  data class Error(val message: String) : ScratchUiState
}

@HiltViewModel
class ScratchViewModel @Inject constructor(
  cardRepository: CardRepository,
  private val coordinator: ScratchCardCoordinator,
) : ViewModel() {

  private var scratchJob: Job? = null

  val cardState: StateFlow<CardState> = cardRepository.cardState
  val scratchCode: StateFlow<String?> = cardRepository.scratchCode

  private val _scratchState = MutableStateFlow<ScratchUiState>(ScratchUiState.Idle)
  val scratchState: StateFlow<ScratchUiState> = _scratchState.asStateFlow()

  fun scratchCard() {
    if (scratchState.value !is ScratchUiState.Loading && cardState.value == CardState.UNSCRATCHED) {
      _scratchState.value = ScratchUiState.Loading
      scratchJob?.cancel()
      scratchJob = viewModelScope.launch {
        try {
          val code = coordinator.scratchCard()
          _scratchState.value = ScratchUiState.Success(code)
        } catch (t: CancellationException) {
          throw t
        } catch (t: Throwable) {
          _scratchState.value = ScratchUiState.Error(t.message ?: "Unknown error")
        }
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    scratchJob?.cancel()
  }
}
