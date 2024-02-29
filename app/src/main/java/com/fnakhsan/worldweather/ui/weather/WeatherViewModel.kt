package com.fnakhsan.worldweather.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fnakhsan.core.data.base.DataResource
import com.fnakhsan.core.domain.model.WeatherModel
import com.fnakhsan.core.domain.usecase.weather.WeatherUseCase
import com.fnakhsan.worldweather.ui.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherUseCase: WeatherUseCase) :
    ViewModel() {

    private val _data = MutableLiveData<UiState<List<WeatherModel>>>()
    val data: LiveData<UiState<List<WeatherModel>>> = _data

    fun searchWeather(lat: Double, lon: Double) = weatherUseCase.searchWeather(lat, lon)


    fun getFavLocationWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            weatherUseCase.getFavListWeather().collectLatest {
                when (it) {
                    is DataResource.Loading -> {
                        _data.postValue(UiState.Loading)
                    }

                    is DataResource.Success -> {
                        _data.postValue(UiState.Success(it.data))
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
}