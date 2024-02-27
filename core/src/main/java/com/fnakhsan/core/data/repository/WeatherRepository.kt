package com.fnakhsan.core.data.repository

import android.content.Context
import com.fnakhsan.core.data.base.DataResource
import com.fnakhsan.core.data.base.asDataResourceFlow
import com.fnakhsan.core.data.mapper.weatherResponseToModel
import com.fnakhsan.core.data.source.network.ApiService
import com.fnakhsan.core.domain.model.WeatherModel
import com.fnakhsan.core.domain.usecase.weather.IWeatherRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : IWeatherRepository {
    override fun searchWeather(query: String): Flow<DataResource<WeatherModel>> =
        flowOf(weatherResponseToModel(apiService.getWeather(query))).asDataResourceFlow(context)

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