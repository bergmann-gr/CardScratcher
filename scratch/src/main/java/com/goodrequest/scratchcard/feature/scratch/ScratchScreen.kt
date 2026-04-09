package com.goodrequest.scratchcard.feature.scratch

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.goodrequest.scratchcard.card.model.CardState

@Composable
fun ScratchScreen(
  scratchViewModel: ScratchViewModel = hiltViewModel(),
) {
  val scratchState by scratchViewModel.scratchState.collectAsState()

  Box(
    contentAlignment = Center
  ) {

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
      verticalArrangement = spacedBy(16.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      if(scratchState is ScratchUiState.Success) {
        Text(text = "Card Scratched Successfully!", style = MaterialTheme.typography.headlineMedium)
        Text(text = (scratchState as ScratchUiState.Success).code, style = MaterialTheme.typography.bodyLarge)
      } else if (scratchState is ScratchUiState.Error) {
        Text(text = "Failed to scratch the card. Please try again.", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.error)
      }

      Button(
        enabled = scratchState !is ScratchUiState.Loading && scratchState !is ScratchUiState.Success,
        onClick = { scratchViewModel.scratchCard() }
      ) {
        Text(text = "Scratch Card")
      }
    }

    if (scratchState is ScratchUiState.Loading)
      CircularProgressIndicator()
  }
}
