package com.goodrequest.scratchcard.feature.activation

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ActivationScreen(
  viewModel: ActivationViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsState()

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

      Text(
        text = when (val state = uiState) {
          is ActivationUiState.Success -> "Activation Successful: ${state.androidVersion}"
          is ActivationUiState.Error -> "Activation failed."
          is ActivationUiState.Loading -> "Activating..."
          ActivationUiState.Idle -> "To activate your card, press the button below."
        }
      )

      Button(
        onClick = { viewModel.activateCard() },
        enabled = uiState !is ActivationUiState.Loading && uiState !is ActivationUiState.Success
      ) {
        Text(text = "Activate Card")
      }
    }

    if (uiState is ActivationUiState.Loading) {
      CircularProgressIndicator()
    }
  }

  val error = uiState as? ActivationUiState.Error

  if (error is ActivationUiState.Error) {
    AlertDialog(
      onDismissRequest = { viewModel.dismissError() },
      title = { Text(text = "Activation failed") },
      text = { Text(text = error.message) },
      confirmButton = {
        TextButton(onClick = { viewModel.dismissError() }) {
          Text(text = "OK")
        }
      }
    )
  }
}
