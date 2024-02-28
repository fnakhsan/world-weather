package com.fnakhsan.core.di.features

import com.fnakhsan.core.data.repository.WeatherRepository
import com.fnakhsan.core.di.NetworkModule
import com.fnakhsan.core.domain.usecase.weather.IWeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module(includes = [NetworkModule::class])
abstract class WeatherModule {
    @Binds
    abstract fun provideRepository(weatherRepository: WeatherRepository): IWeatherRepository
}