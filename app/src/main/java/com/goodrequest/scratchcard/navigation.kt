package com.goodrequest.scratchcard

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.goodrequest.scratchcard.ui.activation.ActivationScreen
import com.goodrequest.scratchcard.ui.main.MainScreen
import com.goodrequest.scratchcard.ui.scratch.ScratchScreen

const val MAIN_ROUTE = "main"
const val SCRATCH_ROUTE = "scratch"
const val ACTIVATION_ROUTE = "activation"

@Composable
fun NavHost() {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = MAIN_ROUTE) {
    composable(MAIN_ROUTE) { MainScreen(navController) }
    composable(SCRATCH_ROUTE) { ScratchScreen() }
    composable(ACTIVATION_ROUTE) { ActivationScreen() }
  }
}
