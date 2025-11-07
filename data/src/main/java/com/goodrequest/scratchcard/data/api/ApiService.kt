package com.goodrequest.scratchcard.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
  @GET("version")
  suspend fun checkVersion(@Query("code") code: String): ActivationResponseDto
}
