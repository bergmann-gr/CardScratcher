package com.goodrequest.scratchcard.domain

sealed interface State<out E, out V>
object Loading : State<Nothing, Nothing>
data class Content<out V>(val value: V) : State<Nothing, V>
data class Failure<out E>(val value: E) : State<E, Nothing>
