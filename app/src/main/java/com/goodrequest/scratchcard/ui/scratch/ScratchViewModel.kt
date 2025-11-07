package com.goodrequest.scratchcard.ui.scratch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodrequest.scratchcard.domain.CardState
import com.goodrequest.scratchcard.domain.Content
import com.goodrequest.scratchcard.domain.Failure
import com.goodrequest.scratchcard.domain.Loading
import com.goodrequest.scratchcard.domain.ScratchCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.goodrequest.scratchcard.domain.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScratchViewModel @Inject constructor(
  private val repository: ScratchCardRepository
) : ViewModel() {

  private var scratchJob: Job? = null

  val cardState: StateFlow<CardState> = repository.cardState
  val scratchCode: StateFlow<String?> = repository.scratchCode

  val _scratchState = MutableStateFlow<State<Throwable, String>?>(null)
  val scratchState: StateFlow<State<Throwable, String>?> = _scratchState

  fun scratchCard() {
    if(scratchState.value !is Loading && cardState.value == CardState.UNSCRATCHED) {
      _scratchState.value = Loading
      scratchJob?.cancel()
      scratchJob = viewModelScope.launch {
        _scratchState.value = try {
          Content(repository.scratchCard())
        } catch (t: Throwable) {
          Failure(t)
        }
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    scratchJob?.cancel()
  }
}
