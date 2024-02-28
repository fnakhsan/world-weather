package com.fnakhsan.core.data.mapper

import com.fnakhsan.core.data.model.weather.WeatherResponse
import com.fnakhsan.core.domain.model.WeatherModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun weatherResponseToModel(response: WeatherResponse): WeatherModel {
    val weather = response.weather.first()
    val main = response.main
    return WeatherModel(
        id = response.id.toString(),
        location = response.name,
        iconUrl = iconUrlMapper(weather.id),
        description = weather.description,
        datetime = Instant.ofEpochMilli(response.dt.toLong()).atZone(
            ZoneId.systemDefault()
        ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
        temperature = main.temp,
        feelsLike = main.feelsLike,
        pressure = main.pressure.toDouble(),
        humidity = main.humidity.toDouble(),
        visibility = response.visibility.toDouble(),
        cloudiness = response.clouds.all.toDouble(),
        windSpeed = response.wind.speed,
        windGust = response.wind.gust
    )
}