package com.goodrequest.scratchcard.ui.main

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.goodrequest.scratchcard.card.model.CardState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  onScratchClick: () -> Unit,
  onActivationClick: () -> Unit,
  viewModel: MainViewModel = hiltViewModel(),
) {
  val cardState by viewModel.cardState.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(title = { Text("Scratch Card") })
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      verticalArrangement = spacedBy(16.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      Text(
        text = when (cardState) {
          is CardState.Unscratched -> "Card is unscratched"
          is CardState.Scratched -> "Card is scratched"
          is CardState.Activated -> "Card is activated"
        }
      )

      Text(
        text = when (val state = cardState) {
          is CardState.Unscratched -> ""
          is CardState.Scratched -> state.code
          is CardState.Activated -> state.code
        }
      )

      Button(
        onClick = onScratchClick,
        enabled = cardState is CardState.Unscratched
      ) {
        Text(text = "Go to Scratch")
      }

      Button(
        onClick = onActivationClick,
        enabled = cardState is CardState.Scratched
      ) {
        Text(text = "Go to Activation")
      }

      Button(
        onClick = { viewModel.resetCard() },
        enabled = cardState !is CardState.Unscratched
      ) {
        Text(text = "Reset")
      }
    }
  }
}
