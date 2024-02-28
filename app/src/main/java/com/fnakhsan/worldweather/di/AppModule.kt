package com.fnakhsan.worldweather.di

import com.fnakhsan.core.domain.usecase.weather.WeatherInteractor
import com.fnakhsan.core.domain.usecase.weather.WeatherUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun provideWeatherUseCase(weatherInteractor: WeatherInteractor): WeatherUseCase
}