package com.goodrequest.scratchcard.feature.scratch.data

import com.goodrequest.scratchcard.scratch.api.ScratchCodeGenerator
import kotlinx.coroutines.delay
import java.util.UUID

class ScratchCodeGeneratorImpl : ScratchCodeGenerator {
  override suspend fun generate(): String {
    delay(2000)
    return UUID.randomUUID().toString()
  }
}
