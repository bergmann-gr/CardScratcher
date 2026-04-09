package com.goodrequest.scratchcard.scratch.api

interface ScratchCodeGenerator {
  suspend fun generate(): String
}

