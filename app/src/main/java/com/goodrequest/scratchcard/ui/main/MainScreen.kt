package com.goodrequest.scratchcard.ui.main

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.goodrequest.scratchcard.card.model.CardState

@Composable
fun MainScreen(
  onScratchClick: () -> Unit,
  onActivationClick: () -> Unit,
  viewModel: MainViewModel = hiltViewModel(),
) {
  val state by viewModel.cardState.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = spacedBy(16.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {

    Text(text = state.toString()) //TODO

    Text(text = when (val state = state) {
      is CardState.Unscratched -> ""
      is CardState.Scratched -> state.code
      is CardState.Activated -> state.code
    })

    Button(
      onClick = onScratchClick,
      enabled = state is CardState.Unscratched
    ) {
      Text(text = "Go to Scratch")
    }

    Button(
      onClick = onActivationClick,
      enabled = state is CardState.Scratched
    ) {
      Text(text = "Go to Activation")
    }

    Button(
      onClick = { viewModel.resetCard()  },
      enabled = state !is CardState.Unscratched
    ) {
      Text(text = "Reset")
    }
  }
}
