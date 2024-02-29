package com.fnakhsan.core.domain.usecase.weather

import com.fnakhsan.core.data.base.DataResource
import com.fnakhsan.core.domain.model.WeatherModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherInteractor @Inject constructor(private val weatherRepository: IWeatherRepository): WeatherUseCase {
    override fun searchWeather(query: String): Flow<DataResource<WeatherModel?>> {
        return weatherRepository.searchWeather(query)
    }

    override fun searchWeather(lat: Double, lon: Double): Flow<DataResource<WeatherModel?>> {
        return weatherRepository.searchWeather(lat, lon)
    }

    override fun getFavListWeather(): Flow<DataResource<List<WeatherModel>>> {
        return weatherRepository.getFavListWeather()
    }

    override fun setFavWeather(weatherModel: WeatherModel) {
        return weatherRepository.setFavWeather(weatherModel)
    }

    override fun isFavWeather(id: Int): Flow<Boolean> {
        return weatherRepository.isFavWeather(id = id)
    }

    override fun getLocation(): String? {
        return weatherRepository.getLocation()
    }
}