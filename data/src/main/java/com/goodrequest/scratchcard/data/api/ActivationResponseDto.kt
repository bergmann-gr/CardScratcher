package com.goodrequest.scratchcard.data.api

import com.goodrequest.scratchcard.domain.ActivationResponse
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ActivationResponseDto(val android: String)

fun ActivationResponseDto.toDomain() = ActivationResponse(code = this.android)
