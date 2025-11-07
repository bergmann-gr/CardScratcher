package com.goodrequest.scratchcard.data

import com.goodrequest.scratchcard.domain.CardState
import com.goodrequest.scratchcard.domain.Content
import com.goodrequest.scratchcard.domain.Failure
import com.goodrequest.scratchcard.domain.Loading
import com.goodrequest.scratchcard.domain.ScratchCardRepository
import com.goodrequest.scratchcard.domain.State
import com.goodrequest.scratchcard.data.api.ApiService
import com.goodrequest.scratchcard.data.api.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScratchCardRepositoryImpl @Inject constructor(
  private val apiService: ApiService,
  dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ScratchCardRepository {

  private val scope = CoroutineScope(SupervisorJob() + dispatcher)

  private val _cardState = MutableStateFlow(CardState.UNSCRATCHED)
  override val cardState: StateFlow<CardState> = _cardState

  private val _scratchCode = MutableStateFlow<String?>(null)
  override val scratchCode: StateFlow<String?> = _scratchCode

  private val _activationState = MutableStateFlow<State<Throwable, String>?>(null)
  override val activationState: StateFlow<State<Throwable, String>?> = _activationState.asStateFlow()


  private var scratchJob: Job? = null

  override suspend fun scratchCard(): String {
    scratchJob?.cancel()
    scratchJob = scope.launch {
      delay(2000)
    }
    scratchJob?.join()
    val code = UUID.randomUUID().toString()
    _scratchCode.value = code
    _cardState.value = CardState.SCRATCHED
    return code
  }

  override fun cancelScratch() {
    scratchJob?.cancel()
  }

  override fun activateCard() {
    scope.launch {
      val scratchCodeValue = scratchCode.value
      if (scratchCodeValue != null) {
        _activationState.value = Loading
        try {
          val responseDto = apiService.checkVersion(scratchCodeValue)
          val response = responseDto.toDomain()

          val code = response.code.toIntOrNull() ?: 0
          val success = code > 277028
          if (success) {
            _cardState.value = CardState.ACTIVATED
            _activationState.value = Content(response.code)
          } else {
            _activationState.value = Failure(Throwable("Activation failed : $code"))
          }
        } catch (t: Throwable) {
          _activationState.value = Failure(t)
        }
      } else {
        _activationState.value = Failure(IllegalStateException("No scratch code available for activation"))
      }
    }
  }
}
