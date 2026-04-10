package com.goodrequest.scratchcard.di

import com.goodrequest.scratchcard.activation.api.CardActivator
import com.goodrequest.scratchcard.card.CardManager
import com.goodrequest.scratchcard.card.CardRepository
import com.goodrequest.scratchcard.card.CardRepositoryImpl
import com.goodrequest.scratchcard.feature.activation.data.ApiService
import com.goodrequest.scratchcard.feature.activation.data.CardActivatorImpl
import com.goodrequest.scratchcard.feature.scratch.data.ScratchCodeGeneratorImpl
import com.goodrequest.scratchcard.scratch.api.ScratchCodeGenerator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppCardModule {

  @Provides
  @Singleton
  fun provideMoshi(): Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

  @Provides
  @Singleton
  fun provideRetrofit(moshi: Moshi): Retrofit = Retrofit.Builder()
    .baseUrl("https://api.o2.sk/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

  @Provides
  @Singleton
  fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

  @Provides
  @Singleton
  fun provideCardActivator(
    apiService: ApiService,
  ): CardActivator = CardActivatorImpl(apiService)

  @Provides
  @Singleton
  fun provideScratchCodeGenerator(): ScratchCodeGenerator = ScratchCodeGeneratorImpl()

  @Provides
  @Singleton
  fun provideCardRepository(): CardRepository = CardRepositoryImpl()

  @Provides
  @Singleton
  fun provideCardManager(
    cardRepository: CardRepository,
    scratchCodeGenerator: ScratchCodeGenerator,
    cardActivator: CardActivator,
  ): CardManager = CardManager(
    cardRepository = cardRepository,
    scratchCodeGenerator = scratchCodeGenerator,
    cardActivator = cardActivator,
  )
}
