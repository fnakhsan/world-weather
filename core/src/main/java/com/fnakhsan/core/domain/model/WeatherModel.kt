package com.fnakhsan.core.domain.model

data class WeatherModel(
    val id: String,
    val location: String,
    val iconUrl: String,
    val description: String,
    val datetime: String,
    val temperature: Double,
    val feelsLike: Double,
    val pressure: Double,
    val humidity: Double,
    val visibility: Double,
    val cloudiness: Double,
    val windSpeed: Double,
    val windGust: Double,
)
