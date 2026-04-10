package com.goodrequest.scratchcard.card

import com.goodrequest.scratchcard.activation.api.ActivationResult
import com.goodrequest.scratchcard.activation.api.CardActivator
import com.goodrequest.scratchcard.card.model.CardState
import com.goodrequest.scratchcard.scratch.api.ScratchCodeGenerator
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class CardManager(
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
    if (cardRepository.cardState.value is CardState.Activated) {
      return ActivationResult.Activated(
        androidVersion =
          (cardRepository.cardState.value as CardState.Activated).activationNumber
      )
    }

    val cardState = cardRepository.cardState.value as? CardState.Scratched
    val code = cardState?.code ?: return ActivationResult.MissingCode

    return withContext(NonCancellable) {
      when (val result = cardActivator.activate(code)) {
        is ActivationResult.Activated -> {
          cardRepository.markActivated(code, result.androidVersion)
          result
        }

        else -> result
      }
    }
  }
}
