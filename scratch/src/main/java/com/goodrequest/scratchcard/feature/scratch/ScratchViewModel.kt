package com.goodrequest.scratchcard.feature.scratch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodrequest.scratchcard.card.CardManager
import com.goodrequest.scratchcard.card.CardRepository
import com.goodrequest.scratchcard.card.model.CardState
import com.goodrequest.scratchcard.feature.scratch.data.ScratchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScratchViewModel @Inject constructor(
  cardRepository: CardRepository,
  private val cardManager: CardManager,
) : ViewModel() {

  private var scratchJob: Job? = null

  val cardState: StateFlow<CardState> = cardRepository.cardState

  private val _scratchState = MutableStateFlow<ScratchUiState>(ScratchUiState.Idle)
  val scratchState: StateFlow<ScratchUiState> = _scratchState.asStateFlow()

  fun scratchCard() {
    if (scratchState.value !is ScratchUiState.Loading && cardState.value is CardState.Unscratched) {
      _scratchState.value = ScratchUiState.Loading
      scratchJob?.cancel()
      scratchJob = viewModelScope.launch {
        try {
          val code = cardManager.scratchCard()
          _scratchState.value = ScratchUiState.Success(code)
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
