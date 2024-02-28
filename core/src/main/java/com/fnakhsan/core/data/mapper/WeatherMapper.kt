package com.fnakhsan.core.data.mapper

import com.fnakhsan.core.data.model.weather.WeatherResponse
import com.fnakhsan.core.domain.model.WeatherModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun weatherResponseToModel(response: WeatherResponse): WeatherModel {
    val weather = response.weather?.first()
    val main = response.main
    return WeatherModel(
        id = response.id.toString(),
        location = response.name ?: "",
        iconUrl = iconUrlMapper(weather?.icon ?: ""),
        description = weather?.description ?: "",
        datetime = Instant.ofEpochMilli(response.dt?.toLong() ?: 0).atZone(
            ZoneId.systemDefault()
        ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
        temperature = main?.temp ?: 0.0,
        feelsLike = main?.feelsLike ?: 0.0,
        humidity = main?.humidity ?: 0,
        visibility = response.visibility ?: 0,
        cloudiness = response.clouds?.all ?: 0,
        windSpeed = response.wind?.speed ?: 0.0,
    )
}