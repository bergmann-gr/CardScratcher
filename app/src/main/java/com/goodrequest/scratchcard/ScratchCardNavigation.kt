package com.goodrequest.scratchcard

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.goodrequest.scratchcard.feature.activation.ActivationScreen
import com.goodrequest.scratchcard.feature.scratch.ScratchScreen
import com.goodrequest.scratchcard.ui.main.MainScreen
import kotlinx.serialization.Serializable

@Serializable
private sealed interface ScratchCardDestination : NavKey {
  @Serializable
  data object Main : ScratchCardDestination
  @Serializable
  object Scratch : ScratchCardDestination
  @Serializable
  data object Activation : ScratchCardDestination
}

@Composable
fun ScratchCardNav() {
  val backStack = rememberNavBackStack(ScratchCardDestination.Main)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeAt(backStack.lastIndex) },
    entryDecorators = listOf(
      rememberSaveableStateHolderNavEntryDecorator(),
      rememberViewModelStoreNavEntryDecorator(),
    ),
    entryProvider = entryProvider {
      entry<ScratchCardDestination.Main> {
        MainScreen(
          onScratchClick = { backStack.add(ScratchCardDestination.Scratch) },
          onActivationClick = { backStack.add(ScratchCardDestination.Activation) },
        )
      }
      entry<ScratchCardDestination.Scratch> { ScratchScreen() }
      entry<ScratchCardDestination.Activation> { ActivationScreen() }
    },
  )
}
