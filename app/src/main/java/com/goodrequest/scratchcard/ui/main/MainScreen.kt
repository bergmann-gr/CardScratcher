package com.goodrequest.scratchcard.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.goodrequest.scratchcard.card.model.CardState

@Composable
fun MainScreen(
  navController: NavHostController,
  scratchRoute: String,
  activationRoute: String,
  viewModel: MainViewModel = hiltViewModel(),
) {
  val state by viewModel.cardState.collectAsState()
  val code by viewModel.scratchCode.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(state.name)
    Text(code ?: "")
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = { navController.navigate(scratchRoute) }) {
      Text("Go to Scratch")
    }
    Spacer(modifier = Modifier.height(16.dp))
    Button(
      onClick = { navController.navigate(activationRoute) },
      enabled = state == CardState.SCRATCHED
    ) {
      Text("Go to Activation")
    }
    Spacer(modifier = Modifier.height(16.dp))
      Button(
        onClick = { viewModel.resetCard()  },
        enabled = state != CardState.UNSCRATCHED
      ) {
        Text("Reset")
      }
  }
}
