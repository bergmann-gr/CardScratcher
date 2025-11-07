package com.goodrequest.scratchcard.ui.main

import androidx.lifecycle.ViewModel
import com.goodrequest.scratchcard.domain.CardState
import com.goodrequest.scratchcard.domain.ScratchCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  repository: ScratchCardRepository
) : ViewModel() {
  val cardState: StateFlow<CardState> = repository.cardState
}
