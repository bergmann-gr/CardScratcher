package com.goodrequest.scratchcard.feature.activation.data

import com.goodrequest.scratchcard.activation.api.ActivationResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActivationRepositoryImplTest {

  @Test
  fun `activate returns Activated when android version is high enough`() = runTest {
    val apiService = FakeApiService(response = ActivationResponseDto(android = "300000"))
    val activator = CardActivatorImpl(apiService)

    assertEquals(ActivationResult.Activated(300000), activator.activate("code"))
  }

  @Test
  fun `activate returns Rejected when android version is too low`() = runTest {
    val apiService = FakeApiService(response = ActivationResponseDto(android = "277028"))
    val activator = CardActivatorImpl(apiService)

    assertEquals(ActivationResult.Rejected(277028), activator.activate("code"))
  }

  @Test
  fun `activate returns Rejected when android version is not a number`() = runTest {
    val apiService = FakeApiService(response = ActivationResponseDto(android = "not-a-number"))
    val activator = CardActivatorImpl(apiService)

    assertEquals(ActivationResult.Rejected(0), activator.activate("code"))
  }

  @Test
  fun `activate returns Failure on exception`() = runTest {
    val throwable = RuntimeException("fail")
    val apiService = FakeApiService(throwable = throwable)
    val activator = CardActivatorImpl(apiService)

    assertEquals(ActivationResult.Failure(throwable), activator.activate("code"))
  }

  private class FakeApiService(
    private val response: ActivationResponseDto? = null,
    private val throwable: Throwable? = null,
  ) : ApiService {
    override suspend fun checkVersion(code: String): ActivationResponseDto {
      throwable?.let { throw it }
      return response ?: throw IllegalStateException("No response configured")
    }
  }
}

