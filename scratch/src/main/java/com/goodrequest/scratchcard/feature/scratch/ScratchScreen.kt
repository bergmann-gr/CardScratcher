package com.goodrequest.scratchcard.feature.scratch

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScratchScreen(
  onBack: () -> Unit = {},
  scratchViewModel: ScratchViewModel = hiltViewModel(),
) {
  val scratchState by scratchViewModel.scratchState.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Scratch Card") },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back"
            )
          }
        }
      )
    }
  ) { innerPadding ->
    Box(
      contentAlignment = Center,
      modifier = Modifier.padding(innerPadding)
    ) {

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        verticalArrangement = spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {

      if(scratchState is ScratchUiState.Success) {
        Text(text = "Card Scratched Successfully!")
        Text(text = (scratchState as ScratchUiState.Success).code, style = MaterialTheme.typography.bodyMedium)
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
}
