package com.goodrequest.scratchcard.feature.scratch.data

sealed interface ScratchUiState {
  data object Idle : ScratchUiState
  data object Loading : ScratchUiState
  data class Success(val code: String) : ScratchUiState
  data class Error(val message: String) : ScratchUiState
}