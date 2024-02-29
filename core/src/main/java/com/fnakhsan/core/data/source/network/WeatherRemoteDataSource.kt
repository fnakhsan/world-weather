package com.fnakhsan.core.data.source.network

import android.content.Context
import com.fnakhsan.core.data.base.DataResource
import com.fnakhsan.core.data.base.asDataResourceSuspend
import com.fnakhsan.core.data.model.weather.WeatherResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRemoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) {
    fun searchWeather(query: String): Flow<DataResource<WeatherResponse>> = flow {
        val lang = Locale.getDefault().language
        emit(asDataResourceSuspend({
            if (lang == "in") apiService.getWeather(
                query = query,
                language = "id",
                units = "metric"
            )
            else apiService.getWeather(query = query)
        }, context))
    }.flowOn(Dispatchers.IO)

    fun searchWeather(lat: Double, lon: Double): Flow<DataResource<WeatherResponse>> = flow {
        val lang = Locale.getDefault().language
        emit(asDataResourceSuspend({
            if (lang == "in") apiService.getWeather(
                latitude = lat,
                longitude = lon,
                language = "id",
                units = "metric"
            )
            else apiService.getWeather(
                latitude = lat,
                longitude = lon
            )
        }, context))
    }.flowOn(Dispatchers.IO)

//    fun searchWeather(query: String): Flow<DataResource<WeatherModel>> = flow {
//        val lang = Locale.getDefault().language
//        emit(asDataResourceSuspend({
//            weatherResponseToModel(
//                if (lang == "in") apiService.getWeather(
//                    query = query,
//                    language = "id",
//                    units = "metric"
//                )
//                else apiService.getWeather(query = query)
//            )
//        }, context))
//    }.flowOn(Dispatchers.IO)

//    fun searchWeather(lat: Double, lon: Double): Flow<DataResource<WeatherModel>> = flow {
//        val lang = Locale.getDefault().language
//        emit(asDataResourceSuspend({
//            weatherResponseToModel(
//                if (lang == "in") apiService.getWeather(
//                    latitude = lat,
//                    longitude = lon,
//                    language = "id",
//                    units = "metric"
//                )
//                else apiService.getWeather(
//                    latitude = lat,
//                    longitude = lon
//                )
//            )
//        }, context))
//    }.flowOn(Dispatchers.IO)
}