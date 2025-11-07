package com.goodrequest.scratchcard.data

import app.cash.turbine.test
import com.goodrequest.scratchcard.domain.CardState
import com.goodrequest.scratchcard.domain.Content
import com.goodrequest.scratchcard.domain.Failure
import com.goodrequest.scratchcard.domain.Loading
import com.goodrequest.scratchcard.data.api.ActivationResponseDto
import com.goodrequest.scratchcard.data.api.ApiService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException

@OptIn(ExperimentalCoroutinesApi::class)
class ScratchCardRepositoryImplTest {

  private lateinit var apiService: ApiService
  private lateinit var repository: ScratchCardRepositoryImpl
  private val testDispatcher = StandardTestDispatcher()
  private lateinit var testScope: TestScope

  @BeforeEach
  fun setup() {
    MockKAnnotations.init(this)
    apiService = mockk()
    testScope = TestScope(testDispatcher)
    repository = ScratchCardRepositoryImpl(apiService, testDispatcher)
  }

  @Test
  fun `scratchCard sets correct state and code`() = testScope.runTest {
    val deferred = launch {
      val resultCode = repository.scratchCard()
      Assertions.assertEquals(repository.scratchCode.value, resultCode)
    }
    advanceTimeBy(2000)
    deferred.join()

    repository.cardState.test {
      Assertions.assertEquals(CardState.SCRATCHED, awaitItem())
    }
    Assertions.assertNotNull(repository.scratchCode.value)
  }

  @Test
  fun `activateCard success updates state`() = testScope.runTest {
    val fakeResponse = ActivationResponseDto("300000")
    coEvery { apiService.checkVersion(any()) } returns fakeResponse
    repository.scratchCard()
    advanceTimeBy(2000)

    repository.activationState.test {
      Assertions.assertEquals(null, awaitItem())
      repository.activateCard()
      Assertions.assertEquals(Loading, awaitItem())
      advanceUntilIdle()
      Assertions.assertEquals(Content(fakeResponse.android), awaitItem())
      repository.cardState.test {
        Assertions.assertEquals(CardState.ACTIVATED, awaitItem())
      }
    }
  }

  @Test
  fun `activateCard error returned when card not scratched`() = testScope.runTest {
    repository.activateCard()
    advanceUntilIdle()

    repository.activationState.test {
      val state = awaitItem() as? Failure
      Assertions.assertInstanceOf(Failure::class.java, state)
      Assertions.assertInstanceOf(IllegalStateException::class.java, state?.value)
    }
  }

  @Test
  fun `activateCard error updates error state`() = testScope.runTest {
    val fakeException = RuntimeException("fail")
    coEvery { apiService.checkVersion(any()) } throws fakeException
    repository.scratchCard()
    advanceTimeBy(2000)
    repository.activateCard()
    advanceUntilIdle()

    repository.activationState.test {
      Assertions.assertEquals(fakeException, (awaitItem() as Failure).value)
    }
  }

  @AfterEach
  fun tearDown() {
    unmockkAll()
  }
}
