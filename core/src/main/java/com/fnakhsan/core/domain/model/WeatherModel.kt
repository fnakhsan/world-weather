package com.fnakhsan.core.domain.model

data class WeatherModel(
    val id: String,
    val location: String,
    val iconUrl: String,
    val description: String,
    val datetime: String,
    val temperature: Float,
    val feelsLike: Float,
    val pressure: Float,
    val humidity: Float,
    val visibility: Float,
    val cloudiness: Float,
    val windSpeed: Float,
    val windGust: Float,
)
