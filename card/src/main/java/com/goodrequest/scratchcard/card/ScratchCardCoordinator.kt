package com.goodrequest.scratchcard.card

import com.goodrequest.scratchcard.card.model.CardState
import com.goodrequest.scratchcard.activation.api.ActivationResult
import com.goodrequest.scratchcard.activation.api.CardActivator
import com.goodrequest.scratchcard.scratch.api.ScratchCodeGenerator
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class ScratchCardCoordinator(
  private val cardRepository: CardRepository,
  private val scratchCodeGenerator: ScratchCodeGenerator,
  private val cardActivator: CardActivator,
) {
  suspend fun scratchCard(): String {
    val code = scratchCodeGenerator.generate()
    cardRepository.markScratched(code)
    return code
  }

  suspend fun activateCard(): ActivationResult {
    if (cardRepository.cardState.value == CardState.ACTIVATED) {
      return ActivationResult.Activated(androidVersion = 0)
    }

    val code = cardRepository.scratchCode.value ?: return ActivationResult.MissingCode

    return withContext(NonCancellable) {
      when (val result = cardActivator.activate(code)) {
        is ActivationResult.Activated -> {
          cardRepository.markActivated()
          result
        }
        else -> result
      }
    }
  }
}
