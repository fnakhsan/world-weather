package com.fnakhsan.worldweather.ui.weather

import androidx.lifecycle.ViewModel
import com.fnakhsan.core.domain.usecase.weather.WeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherUseCase: WeatherUseCase) : ViewModel() {

}