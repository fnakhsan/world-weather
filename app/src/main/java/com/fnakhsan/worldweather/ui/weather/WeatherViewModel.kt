package com.fnakhsan.worldweather.ui.weather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fnakhsan.core.data.base.DataResource
import com.fnakhsan.core.domain.model.WeatherModel
import com.fnakhsan.core.domain.usecase.weather.WeatherUseCase
import com.fnakhsan.worldweather.ui.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherUseCase: WeatherUseCase) :
    ViewModel() {

    private val _data = MediatorLiveData<UiState<MutableSet<WeatherModel>>>()
    val data: LiveData<UiState<MutableSet<WeatherModel>>> = _data
    private val weatherSet = mutableSetOf<WeatherModel>()

    @OptIn(FlowPreview::class)
    fun searchWeather(query: String) = weatherUseCase.searchWeather(query).debounce(1000).asLiveData(Dispatchers.IO)


    fun searchWeather(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherUseCase.searchWeather(lat, lon).collectLatest {
                when (it) {
                    is DataResource.Loading -> {
                        _data.postValue(UiState.Loading)
                    }

                    is DataResource.Success -> {
                        Log.d("data", "vm lat ${it.data}")
                        if (it.data != null) {
                            weatherSet.add(it.data!!)
                            _data.postValue(UiState.Success(weatherSet))
                        } else _data.postValue(UiState.Empty)
                    }

                    is DataResource.Error -> {
                        if (it.errorCode == "#ER404") {
                            _data.postValue(UiState.Empty)
                        } else {
                            _data.postValue(UiState.Error(it.exception.message, it.errorCode))
                        }
                    }
                }
            }
        }
    }

    fun getFavLocationWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            weatherUseCase.getFavListWeather().collectLatest {
                when (it) {
                    is DataResource.Loading -> {
                        _data.postValue(UiState.Loading)
                    }

                    is DataResource.Success -> {
                        weatherSet.clear()
                        weatherSet.addAll(it.data)
                        _data.postValue(UiState.Success(weatherSet))
                    }

                    is DataResource.Error -> {
                        if (it.errorCode == "#ER404") {
                            _data.postValue(UiState.Empty)
                        } else {
                            _data.postValue(UiState.Error(it.exception.message, it.errorCode))
                        }
                    }
                }
            }
        }
    }

    fun getLocation(): String? = weatherUseCase.getLocation()
}