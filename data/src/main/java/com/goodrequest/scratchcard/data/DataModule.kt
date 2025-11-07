package com.goodrequest.scratchcard.data

import com.goodrequest.scratchcard.domain.ScratchCardRepository
import com.goodrequest.scratchcard.data.api.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

  @Provides
  fun provideIoCoroutineScope() = Dispatchers.IO

  @Provides
  @Singleton
  fun provideMoshi(): Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

  @Provides
  @Singleton
  fun provideApiService(moshi: Moshi): ApiService = Retrofit.Builder()
    .baseUrl("https://api.o2.sk/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()
    .create(ApiService::class.java)

  @Provides
  @Singleton
  fun provideScratchCardRepository(
    apiService: ApiService,
    dispatcher: CoroutineDispatcher,
  ): ScratchCardRepository {
    return ScratchCardRepositoryImpl(apiService, dispatcher)
  }
}
