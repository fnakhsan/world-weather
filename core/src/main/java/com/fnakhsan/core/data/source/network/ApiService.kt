package com.fnakhsan.core.data.source.network

import com.fnakhsan.core.BuildConfig
import com.fnakhsan.core.data.model.weather.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.Flow

interface ApiService {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") query: String,
        @Query("appid") apiKey: String = BuildConfig.API_KEY,
        @Query("lang") language: String? = null,
        @Query("units") units: String? = null
    ): WeatherResponse

    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = BuildConfig.API_KEY,
        @Query("lang") language: String? = null,
        @Query("units") units: String? = null
    ): WeatherResponse
}