package com.fnakhsan.core.data.source.network

import com.fnakhsan.core.BuildConfig
import com.fnakhsan.core.data.model.geocoding.GeocodingResponse
import com.fnakhsan.core.data.model.geocoding.ReverseGeocodingResponse
import com.fnakhsan.core.data.model.weather.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

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

    @GET("geo/1.0/reverse")
    suspend fun getLocationName(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = BuildConfig.API_KEY,
    ): ReverseGeocodingResponse

    @GET("geo/1.0/direct")
    suspend fun getLatLon(
        @Query("q") query: String,
        @Query("appid") apiKey: String = BuildConfig.API_KEY,
    ): GeocodingResponse
}