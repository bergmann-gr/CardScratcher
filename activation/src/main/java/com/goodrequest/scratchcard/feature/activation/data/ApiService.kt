package com.goodrequest.scratchcard.feature.activation.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
  @GET("version")
  suspend fun checkVersion(@Query("code") code: String): ActivationResponseDto
}

