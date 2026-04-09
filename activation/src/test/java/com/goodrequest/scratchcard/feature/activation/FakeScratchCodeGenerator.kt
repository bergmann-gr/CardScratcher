package com.goodrequest.scratchcard.feature.activation

import com.goodrequest.scratchcard.scratch.api.ScratchCodeGenerator

class FakeScratchCodeGenerator(val code: String): ScratchCodeGenerator {
  override suspend fun generate(): String {
    return code
  }
}
