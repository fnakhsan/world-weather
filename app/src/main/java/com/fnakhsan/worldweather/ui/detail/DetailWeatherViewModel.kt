package com.fnakhsan.worldweather.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fnakhsan.core.domain.model.WeatherModel
import com.fnakhsan.core.domain.usecase.weather.WeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailWeatherViewModel @Inject constructor(private val weatherUseCase: WeatherUseCase) :
    ViewModel() {

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun isFavorite(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherUseCase.isFavWeather(id = id).collectLatest {
                _isFavorite.postValue(it)
            }
        }
    }

    fun setFavorite(weatherModel: WeatherModel, favorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherUseCase.setFavWeather(weatherModel= weatherModel, favorite)
            _isFavorite.postValue(favorite)
        }
    }
}