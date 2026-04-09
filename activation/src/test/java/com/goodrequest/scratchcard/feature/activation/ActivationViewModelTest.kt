package com.goodrequest.scratchcard.feature.activation

import com.goodrequest.scratchcard.activation.api.ActivationResult
import com.goodrequest.scratchcard.activation.api.CardActivator
import com.goodrequest.scratchcard.card.CardRepositoryImpl
import com.goodrequest.scratchcard.card.ScratchCardCoordinator
import com.goodrequest.scratchcard.scratch.api.ScratchCodeGenerator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActivationViewModelTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun `activateCard success sets Success state`() = runTest(mainDispatcherRule.dispatcher) {
    val cardRepository = CardRepositoryImpl().apply { markScratched("code") }
    val scratchCodeGenerator = object : ScratchCodeGenerator {
      override suspend fun generate(): String = "unused"
    }
    val cardActivator = FakeCardActivator(ActivationResult.Activated(300000))
    val coordinator = ScratchCardCoordinator(cardRepository, scratchCodeGenerator, cardActivator)
    val viewModel = ActivationViewModel(coordinator)

    viewModel.activateCard()
    assertEquals(ActivationUiState.Loading, viewModel.uiState.value)

    advanceUntilIdle()
    assertEquals(ActivationUiState.Success(300000), viewModel.uiState.value)
  }

  @Test
  fun `activateCard without code sets Error state`() = runTest(mainDispatcherRule.dispatcher) {
    val cardRepository = CardRepositoryImpl()
    val scratchCodeGenerator = FakeScratchCodeGenerator("unused")
    val cardActivator = FakeCardActivator(ActivationResult.Activated(300000))
    val coordinator = ScratchCardCoordinator(cardRepository, scratchCodeGenerator, cardActivator)
    val viewModel = ActivationViewModel(coordinator)

    viewModel.activateCard()
    advanceUntilIdle()

    assertEquals(
      ActivationUiState.Error("No scratch code available for activation"),
      viewModel.uiState.value
    )
  }

  @Test
  fun `activateCard failure sets Error state with message`() = runTest(mainDispatcherRule.dispatcher) {
    val cardRepository = CardRepositoryImpl().apply { markScratched("code") }
    val scratchCodeGenerator = FakeScratchCodeGenerator("unused")
    val cardActivator = object : CardActivator {
      override suspend fun activate(code: String): ActivationResult =
        ActivationResult.Failure(IllegalStateException("boom"))
    }
    val coordinator = ScratchCardCoordinator(cardRepository, scratchCodeGenerator, cardActivator)
    val viewModel = ActivationViewModel(coordinator)

    viewModel.activateCard()
    advanceUntilIdle()

    assertEquals(ActivationUiState.Error("boom"), viewModel.uiState.value)
  }
}

