package com.goodrequest.scratchcard.card

import com.goodrequest.scratchcard.card.model.CardState
import kotlinx.coroutines.flow.StateFlow

interface CardRepository {
  val cardState: StateFlow<CardState>

  fun markScratched(code: String)
  fun markActivated(code: String)
  fun reset()
}

