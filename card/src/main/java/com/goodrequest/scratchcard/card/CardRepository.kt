package com.goodrequest.scratchcard.card

import com.goodrequest.scratchcard.card.model.CardState
import kotlinx.coroutines.flow.StateFlow

interface CardRepository {
  val cardState: StateFlow<CardState>
  val scratchCode: StateFlow<String?>

  fun markScratched(code: String)
  fun markActivated()
  fun reset()
}

