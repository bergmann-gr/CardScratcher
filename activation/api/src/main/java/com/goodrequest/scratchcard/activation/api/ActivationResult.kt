package com.goodrequest.scratchcard.activation.api

sealed interface ActivationResult {
  data class Activated(val androidVersion: Int) : ActivationResult
  data class Rejected(val androidVersion: Int) : ActivationResult
  data object MissingCode : ActivationResult
  data class Failure(val throwable: Throwable) : ActivationResult
}

