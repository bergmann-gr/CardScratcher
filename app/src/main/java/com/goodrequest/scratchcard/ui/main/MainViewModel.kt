package com.goodrequest.scratchcard.ui.main

import androidx.lifecycle.ViewModel
import com.goodrequest.scratchcard.card.CardRepository
import com.goodrequest.scratchcard.card.model.CardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val repository: CardRepository,
) : ViewModel() {
  val cardState: StateFlow<CardState> = repository.cardState

  fun resetCard() {
    repository.reset()
  }
}
