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
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CardManagerTest {

  private val testDispatcher = StandardTestDispatcher()
  private val testScope = TestScope(testDispatcher)

  @Test
  fun `scratchCard marks card scratched and stores code`() = testScope.runTest {
    val cardRepository = CardRepositoryImpl()
    val scratchCodeGenerator = FakeScratchCodeGenerator("code-123")
    val cardActivator = FakeCardActivator(ActivationResult.Failure(Throwable("unused")))
    val manager = CardManager(cardRepository, scratchCodeGenerator, cardActivator)
    val code = manager.scratchCard()

    assertEquals("code-123", code)
    assertEquals(CardState.Scratched("code-123"), cardRepository.cardState.value)
    assertEquals("code-123", (cardRepository.cardState.value as? CardState.Scratched)?.code)
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
    val cardActivator = FakeCardActivator(ActivationResult.Failure(Throwable("unused")))
    val manager = CardManager(cardRepository, scratchCodeGenerator, cardActivator)

    val job = launch { manager.scratchCard() }
    advanceTimeBy(1000)
    job.cancel()

    assertEquals(CardState.Unscratched, cardRepository.cardState.value)
  }

  @Test
  fun `activateCard without code returns MissingCode`() = testScope.runTest {
    val cardRepository = CardRepositoryImpl()
    val scratchCodeGenerator = FakeScratchCodeGenerator("unused")
    val cardActivator = FakeCardActivator(ActivationResult.Activated(300000))
    val manager = CardManager(cardRepository, scratchCodeGenerator, cardActivator)

    val result = manager.activateCard()

    assertEquals(ActivationResult.MissingCode, result)
  }

  @Test
  fun `activateCard rejected keeps card scratched`() = testScope.runTest {
    val cardRepository = CardRepositoryImpl().apply { markScratched("code") }
    val scratchCodeGenerator = FakeScratchCodeGenerator("unused")
    val cardActivator = FakeCardActivator(ActivationResult.Rejected(277028))
    val manager = CardManager(cardRepository, scratchCodeGenerator, cardActivator)

    val result = manager.activateCard()

    assertEquals(ActivationResult.Rejected(277028), result)
    assertEquals(CardState.Scratched("code"), cardRepository.cardState.value)
  }

  @Test
  fun `activateCard success marks card activated`() = testScope.runTest {
    val cardRepository = CardRepositoryImpl().apply { markScratched("code") }
    val scratchCodeGenerator = FakeScratchCodeGenerator("unused")
    val cardActivator = FakeCardActivator(ActivationResult.Activated(300000))
    val manager = CardManager(cardRepository, scratchCodeGenerator, cardActivator)

    val result = manager.activateCard()

    assertEquals(ActivationResult.Activated(300000), result)
    assertEquals(CardState.Activated("code", 300000), cardRepository.cardState.value)
  }

  @Test
  fun `activateCard continues even if caller is cancelled`() = testScope.runTest {
    val cardRepository = CardRepositoryImpl().apply { markScratched("code") }
    val scratchCodeGenerator = FakeScratchCodeGenerator("unused")
    val cardActivator = object : CardActivator {
      override suspend fun activate(code: String): ActivationResult {
        delay(2000)
        return ActivationResult.Activated(300000)
      }
    }

    val manager = CardManager(cardRepository, scratchCodeGenerator, cardActivator)

    val job = launch { manager.activateCard() }
    advanceTimeBy(1000)
    job.cancel()
    advanceTimeBy(2000)

    assertEquals(CardState.Activated("code", 300000), cardRepository.cardState.value)
  }
}
