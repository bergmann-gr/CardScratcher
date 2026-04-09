package com.goodrequest.scratchcard.card

import com.goodrequest.scratchcard.card.model.CardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CardRepositoryImpl : CardRepository {
  private val _cardState = MutableStateFlow(CardState.UNSCRATCHED)
  override val cardState: StateFlow<CardState> = _cardState

  private val _scratchCode = MutableStateFlow<String?>(null)
  override val scratchCode: StateFlow<String?> = _scratchCode

  override fun markScratched(code: String) {
    _scratchCode.value = code
    _cardState.value = CardState.SCRATCHED
  }

  override fun markActivated() {
    _cardState.value = CardState.ACTIVATED
  }

  override fun reset() {
    _scratchCode.value = null
    _cardState.value = CardState.UNSCRATCHED
  }
}

