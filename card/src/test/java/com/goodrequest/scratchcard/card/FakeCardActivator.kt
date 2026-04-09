package com.goodrequest.scratchcard.card

import com.goodrequest.scratchcard.activation.api.ActivationResult
import com.goodrequest.scratchcard.activation.api.CardActivator

class FakeCardActivator(val result: ActivationResult) : CardActivator {
  override suspend fun activate(code: String): ActivationResult {
    return result
  }
}
