package com.fnakhsan.core.domain.usecase.weather

import com.fnakhsan.core.data.base.DataResource
import com.fnakhsan.core.domain.model.WeatherModel
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    fun searchQueryWeather(query: String): Flow<DataResource<WeatherModel?>>

    fun searchWeather(lat: Double, lon: Double): Flow<DataResource<WeatherModel?>>

    fun getFavListWeather(): Flow<DataResource<List<WeatherModel>>>

    fun setFavWeather(weatherModel: WeatherModel, favorite: Boolean)

    fun isFavWeather(id: Int): Flow<Boolean>
}