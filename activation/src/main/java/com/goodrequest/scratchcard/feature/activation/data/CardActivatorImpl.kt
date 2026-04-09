package com.goodrequest.scratchcard.feature.activation.data

import com.goodrequest.scratchcard.activation.api.ActivationResult
import com.goodrequest.scratchcard.activation.api.CardActivator

class CardActivatorImpl(
  private val apiService: ApiService,
) : CardActivator {
  override suspend fun activate(code: String): ActivationResult {
    return try {
      val androidVersion = apiService.checkVersion(code).android.toIntOrNull() ?: 0
      if (androidVersion > 277028) {
        ActivationResult.Activated(androidVersion)
      } else {
        ActivationResult.Rejected(androidVersion)
      }
    } catch (t: Throwable) {
      ActivationResult.Failure(t)
    }
  }
}
