package com.goodrequest.scratchcard.ui.activation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goodrequest.scratchcard.domain.Content
import com.goodrequest.scratchcard.domain.Failure
import com.goodrequest.scratchcard.domain.Loading

@Composable
fun ActivationScreen(
  viewModel: ActivationViewModel = hiltViewModel()
) {
  val requestState by viewModel.requestState.collectAsState()
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
        when (val state = requestState) {
          is Content -> "Activation Successful: ${state.value}"
          is Failure -> "${state.value.message}"
          is Loading -> "Activating..."
          null -> "To activate your card, press the button below."
        }
      )
      Button(
        onClick = { viewModel.activateCard() },
        enabled = requestState == null || requestState is Failure
      ) {
        Text("Activate Card")
      }
    }
    if (requestState is Loading)
      CircularProgressIndicator()
  }
}
