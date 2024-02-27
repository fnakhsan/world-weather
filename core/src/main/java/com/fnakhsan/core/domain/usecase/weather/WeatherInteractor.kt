package com.fnakhsan.core.domain.usecase.weather

import com.fnakhsan.core.data.base.DataResource
import com.fnakhsan.core.domain.model.WeatherModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherInteractor @Inject constructor(private val weatherRepository: IWeatherRepository): WeatherUseCase {
    override fun searchWeather(query: String): Flow<DataResource<WeatherModel>> {
        return weatherRepository.searchWeather(query)
    }

    override fun getFavListWeather(): Flow<DataResource<List<WeatherModel>>> {
        TODO("Not yet implemented")
    }

    override suspend fun setFavWeather(id: String) {
        TODO("Not yet implemented")
    }

    override fun isFavWeather(id: String): Flow<DataResource<Boolean>> {
        TODO("Not yet implemented")
    }
}