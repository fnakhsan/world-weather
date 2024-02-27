package com.fnakhsan.core.domain.usecase.weather

import com.fnakhsan.core.data.base.DataResource
import com.fnakhsan.core.domain.model.WeatherModel
import kotlinx.coroutines.flow.Flow

interface WeatherUseCase {
    fun searchWeather(query: String): Flow<DataResource<WeatherModel>>

    fun getFavListWeather(): Flow<DataResource<List<WeatherModel>>>

    suspend fun setFavWeather(id: String)

    fun isFavWeather(id: String): Flow<DataResource<Boolean>>
}