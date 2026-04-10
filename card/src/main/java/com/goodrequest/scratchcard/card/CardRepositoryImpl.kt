package com.goodrequest.scratchcard.card

import com.goodrequest.scratchcard.card.model.CardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CardRepositoryImpl : CardRepository {
  private val _cardState = MutableStateFlow<CardState>(CardState.Unscratched)
  override val cardState: StateFlow<CardState> = _cardState

  override fun markScratched(code: String) {
    _cardState.value = CardState.Scratched(code)
  }

  override fun markActivated(code: String, activationNumber: Int) {
    _cardState.value = CardState.Activated(code, activationNumber)
  }

  override fun reset() {
    _cardState.value = CardState.Unscratched
  }
}

