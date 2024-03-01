package com.fnakhsan.core.data.mapper

import com.fnakhsan.core.data.model.weather.WeatherEntity
import com.fnakhsan.core.data.model.weather.WeatherResponse
import com.fnakhsan.core.domain.model.WeatherModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.TimeZone

val nowDateTime = LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC).toEpochMilli()

fun localizeDateTime(epochMillis: Long): String = Instant.ofEpochMilli(epochMillis).atZone(
    ZoneId.systemDefault()
).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

fun epochMillisUtc(dateTimeString: String): Long {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = formatter.parse(dateTimeString)

    // Get the system's default time zone
    val defaultTimeZone = TimeZone.getDefault()

    // Create a calendar instance with the parsed date and default time zone
    val calendar = Calendar.getInstance(defaultTimeZone)
    if (date != null) {
        calendar.time = date
    }

    // Set the calendar's time zone to UTC
    calendar.timeZone = TimeZone.getTimeZone("UTC")


    // Get the milliseconds since epoch in UTC
    return calendar.timeInMillis
}


fun weatherResponseToModel(response: WeatherResponse): WeatherModel {
    val weather = response.weather?.first()
    val main = response.main
    return WeatherModel(
        id = response.id ?: 0,
        location = response.name ?: "",
        latitude = response.coord?.lat ?: 0.0,
        longitude = response.coord?.lon ?: 0.0,
        iconUrl = weather?.icon ?: "",
        description = weather?.description ?: "",
        datetime = localizeDateTime(response.dt?.toLong() ?: 0),
        lastUpdate = localizeDateTime(nowDateTime),
        temperature = main?.temp ?: 0.0,
        feelsLike = main?.feelsLike ?: 0.0,
        humidity = main?.humidity ?: 0,
        windSpeed = response.wind?.speed ?: 0.0,
        visibility = response.visibility ?: 0,
        cloudiness = response.clouds?.all ?: 0,
    )
}

fun mapResponsesToEntities(response: WeatherResponse): WeatherEntity =
    response.let {
        WeatherEntity(
            id = it.id ?: 0,
            location = response.name ?: "",
            latitude = it.coord?.lat ?: 0.0,
            longitude = it.coord?.lon ?: 0.0,
            iconUrl = it.weather?.first()?.icon ?: "",
            description = it.weather?.first()?.description ?: "",
            datetime = it.dt?.toLong() ?: 0,
            lastUpdate = LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC).toEpochMilli(),
            temperature = it.main?.temp ?: 0.0,
            feelsLike = it.main?.feelsLike ?: 0.0,
            humidity = it.main?.humidity ?: 0,
            windSpeed = response.wind?.speed ?: 0.0,
            visibility = response.visibility ?: 0,
            cloudiness = response.clouds?.all ?: 0,
        )
    }

fun mapResponsesToFavEntities(response: WeatherResponse): WeatherEntity =
    response.let {
        WeatherEntity(
            id = it.id ?: 0,
            location = response.name ?: "",
            latitude = it.coord?.lat ?: 0.0,
            longitude = it.coord?.lon ?: 0.0,
            iconUrl = it.weather?.first()?.icon ?: "",
            description = it.weather?.first()?.description ?: "",
            datetime = it.dt?.toLong() ?: 0,
            lastUpdate = LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC).toEpochMilli(),
            temperature = it.main?.temp ?: 0.0,
            feelsLike = it.main?.feelsLike ?: 0.0,
            humidity = it.main?.humidity ?: 0,
            windSpeed = response.wind?.speed ?: 0.0,
            visibility = response.visibility ?: 0,
            cloudiness = response.clouds?.all ?: 0,
            isFavorite = true
        )
    }

fun mapEntitiesToModel(entity: WeatherEntity?): WeatherModel =
    entity.let {
        WeatherModel(
            id = it?.id ?: 0,
            location = it?.location ?: "",
            latitude = it?.latitude ?: 0.0,
            longitude = it?.longitude ?: 0.0,
            iconUrl = it?.iconUrl ?: "",
            description = it?.description ?: "",
            datetime = localizeDateTime(it?.datetime ?: 0),
            lastUpdate = localizeDateTime(it?.lastUpdate ?: 0),
            temperature = it?.temperature ?: 0.0,
            feelsLike = it?.feelsLike ?: 0.0,
            humidity = it?.humidity ?: 0,
            windSpeed = it?.windSpeed ?: 0.0,
            visibility = it?.visibility ?: 0,
            cloudiness = it?.cloudiness ?: 0,
        )
    }

fun mapModelToEntities(weather: WeatherModel): WeatherEntity =
    weather.let {
        WeatherEntity(
            id = it.id,
            location = it.location,
            latitude = it.latitude,
            longitude = it.longitude,
            iconUrl = it.iconUrl,
            description = it.description,
            datetime = epochMillisUtc(it.datetime),
            lastUpdate = epochMillisUtc(it.lastUpdate),
            temperature = it.temperature,
            feelsLike = it.feelsLike,
            humidity = it.humidity,
            windSpeed = it.windSpeed,
            visibility = it.visibility,
            cloudiness = it.cloudiness,
        )
    }

fun mapModelToFavEntities(weather: WeatherModel, favorite: Boolean): WeatherEntity =
    weather.let {
        WeatherEntity(
            id = it.id,
            location = it.location,
            latitude = it.latitude,
            longitude = it.longitude,
            iconUrl = it.iconUrl,
            description = it.description,
            datetime = epochMillisUtc(it.datetime),
            lastUpdate = epochMillisUtc(it.lastUpdate),
            temperature = it.temperature,
            feelsLike = it.feelsLike,
            humidity = it.humidity,
            windSpeed = it.windSpeed,
            visibility = it.visibility,
            cloudiness = it.cloudiness,
            isFavorite = favorite
        )
    }