package com.goodrequest.scratchcard.feature.activation

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun ActivationScreen(
  onBack: () -> Unit = {},
  viewModel: ActivationViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Activation") },
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
