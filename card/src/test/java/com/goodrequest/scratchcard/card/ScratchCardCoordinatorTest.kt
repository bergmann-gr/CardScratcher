package com.goodrequest.scratchcard.card

import com.goodrequest.scratchcard.activation.api.ActivationResult
import com.goodrequest.scratchcard.activation.api.CardActivator
import com.goodrequest.scratchcard.card.model.CardState
import com.goodrequest.scratchcard.scratch.api.ScratchCodeGenerator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScratchCardCoordinatorTest {

  private val testDispatcher = StandardTestDispatcher()
  private val testScope = TestScope(testDispatcher)

  @Test
  fun `scratchCard marks card scratched and stores code`() = testScope.runTest {
    val cardRepository = CardRepositoryImpl()
    val scratchCodeGenerator = object : ScratchCodeGenerator {
      override suspend fun generate(): String = "code-123"
    }
    val cardActivator = object : CardActivator {
      override suspend fun activate(code: String): ActivationResult = ActivationResult.Failure(Throwable("unused"))
    }

    val coordinator = ScratchCardCoordinator(cardRepository, scratchCodeGenerator, cardActivator)

    val code = coordinator.scratchCard()

    assertEquals("code-123", code)
    assertEquals(CardState.SCRATCHED, cardRepository.cardState.value)
    assertEquals("code-123", cardRepository.scratchCode.value)
  }

  @Test
  fun `scratchCard cancellation does not change card state`() = testScope.runTest {
    val cardRepository = CardRepositoryImpl()
    val scratchCodeGenerator = object : ScratchCodeGenerator {
      override suspend fun generate(): String {
        delay(2000)
        return "code-123"
      }
    }
    val cardActivator = object : CardActivator {
      override suspend fun activate(code: String): ActivationResult = ActivationResult.Failure(Throwable("unused"))
    }

    val coordinator = ScratchCardCoordinator(cardRepository, scratchCodeGenerator, cardActivator)

    val job = launch { coordinator.scratchCard() }
    advanceTimeBy(1000)
    job.cancel()
    runCurrent()

    assertEquals(CardState.UNSCRATCHED, cardRepository.cardState.value)
    assertNull(cardRepository.scratchCode.value)
  }

  @Test
  fun `activateCard without code returns MissingCode`() = testScope.runTest {
    val cardRepository = CardRepositoryImpl()
    val scratchCodeGenerator = object : ScratchCodeGenerator {
      override suspend fun generate(): String = "unused"
    }
    val cardActivator = object : CardActivator {
      override suspend fun activate(code: String): ActivationResult = ActivationResult.Activated(300000)
    }

    val coordinator = ScratchCardCoordinator(cardRepository, scratchCodeGenerator, cardActivator)

    val result = coordinator.activateCard()

    assertEquals(ActivationResult.MissingCode, result)
    assertEquals(CardState.UNSCRATCHED, cardRepository.cardState.value)
  }

  @Test
  fun `activateCard rejected keeps card scratched`() = testScope.runTest {
    val cardRepository = CardRepositoryImpl().apply { markScratched("code") }
    val scratchCodeGenerator = object : ScratchCodeGenerator {
      override suspend fun generate(): String = "unused"
    }
    val cardActivator = object : CardActivator {
      override suspend fun activate(code: String): ActivationResult = ActivationResult.Rejected(277028)
    }

    val coordinator = ScratchCardCoordinator(cardRepository, scratchCodeGenerator, cardActivator)

    val result = coordinator.activateCard()

    assertEquals(ActivationResult.Rejected(277028), result)
    assertEquals(CardState.SCRATCHED, cardRepository.cardState.value)
  }

  @Test
  fun `activateCard success marks card activated`() = testScope.runTest {
    val cardRepository = CardRepositoryImpl().apply { markScratched("code") }
    val scratchCodeGenerator = object : ScratchCodeGenerator {
      override suspend fun generate(): String = "unused"
    }
    val cardActivator = object : CardActivator {
      override suspend fun activate(code: String): ActivationResult = ActivationResult.Activated(300000)
    }

    val coordinator = ScratchCardCoordinator(cardRepository, scratchCodeGenerator, cardActivator)

    val result = coordinator.activateCard()

    assertEquals(ActivationResult.Activated(300000), result)
    assertEquals(CardState.ACTIVATED, cardRepository.cardState.value)
  }

  @Test
  fun `activateCard continues even if caller is canceled`() = testScope.runTest {
    val cardRepository = CardRepositoryImpl().apply { markScratched("code") }
    val scratchCodeGenerator = object : ScratchCodeGenerator {
      override suspend fun generate(): String = "unused"
    }
    val cardActivator = object : CardActivator {
      override suspend fun activate(code: String): ActivationResult {
        delay(2000)
        return ActivationResult.Activated(300000)
      }
    }

    val coordinator = ScratchCardCoordinator(cardRepository, scratchCodeGenerator, cardActivator)

    val job = launch { coordinator.activateCard() }
    advanceTimeBy(1000)
    job.cancel()
    advanceTimeBy(2000)
    runCurrent()

    assertEquals(CardState.ACTIVATED, cardRepository.cardState.value)
  }
}
