package com.goodrequest.scratchcard.activation.api

interface CardActivator {
  suspend fun activate(code: String): ActivationResult
}

