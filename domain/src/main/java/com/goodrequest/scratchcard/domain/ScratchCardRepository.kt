package com.goodrequest.scratchcard.domain

import kotlinx.coroutines.flow.StateFlow

interface ScratchCardRepository {
  val cardState: StateFlow<CardState>
  val scratchCode: StateFlow<String?>
  val activationState: StateFlow<State<Throwable, String>?>

  suspend fun scratchCard(): String
  fun cancelScratch()
  fun activateCard()
}
