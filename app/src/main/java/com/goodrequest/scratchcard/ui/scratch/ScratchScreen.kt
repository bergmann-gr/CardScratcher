package com.goodrequest.scratchcard.ui.scratch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.goodrequest.scratchcard.domain.CardState
import com.goodrequest.scratchcard.domain.Loading

@Composable
fun ScratchScreen(
  scratchViewModel: ScratchViewModel = hiltViewModel(),
) {
  val cardState by scratchViewModel.cardState.collectAsState()
  val scratchCode by scratchViewModel.scratchCode.collectAsState()
  val scratchState by scratchViewModel.scratchState.collectAsState()

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
      scratchCode?.let { code ->
        Text(text = "Code Revealed: $code", style = MaterialTheme.typography.bodyLarge)
      }
      Spacer(Modifier.height(16.dp))
      Button(
        enabled = cardState == CardState.UNSCRATCHED && scratchState !is Loading,
        onClick = { scratchViewModel.scratchCard() }
      ) {
        Text("Scratch Card")
      }
    }
    if (scratchState is Loading)
      CircularProgressIndicator()
  }
}
