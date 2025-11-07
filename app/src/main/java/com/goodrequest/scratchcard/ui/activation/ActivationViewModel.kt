package com.goodrequest.scratchcard.ui.activation

import androidx.lifecycle.ViewModel
import com.goodrequest.scratchcard.domain.Loading
import com.goodrequest.scratchcard.domain.ScratchCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivationViewModel @Inject constructor(
  private val repository: ScratchCardRepository,
) : ViewModel() {

  val requestState = repository.activationState

  fun activateCard() {
    if (requestState.value != Loading) {
      repository.activateCard()
    }
  }
}
