package com.goodrequest.scratchcard.feature.activation

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ActivationScreen(
  viewModel: ActivationViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsState()
  var showErrorDialog by remember { mutableStateOf(false) }

  LaunchedEffect(uiState) {
    showErrorDialog = uiState is ActivationUiState.Error
  }

  Box(
    contentAlignment = Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        when (val state = uiState) {
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
        Text("Activate Card")
      }
    }
    if (uiState is ActivationUiState.Loading) {
      CircularProgressIndicator()
    }
  }

  val error = uiState as? ActivationUiState.Error
  if (error != null && showErrorDialog) {
    AlertDialog(
      onDismissRequest = { showErrorDialog = false },
      title = { Text("Activation failed") },
      text = { Text(error.message) },
      confirmButton = {
        TextButton(onClick = { showErrorDialog = false }) {
          Text("OK")
        }
      }
    )
  }
}
