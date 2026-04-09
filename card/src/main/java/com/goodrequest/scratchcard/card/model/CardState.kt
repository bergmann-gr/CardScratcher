package com.goodrequest.scratchcard.card.model

sealed class CardState {
  data object Unscratched: CardState()
  data class Scratched(val code: String): CardState()
  data class Activated(val code: String): CardState()
}

