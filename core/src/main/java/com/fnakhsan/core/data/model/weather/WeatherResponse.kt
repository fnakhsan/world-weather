package com.fnakhsan.core.data.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(

	@SerialName("visibility")
	val visibility: Int,

	@SerialName("timezone")
	val timezone: Int,

	@SerialName("main")
	val main: Main,

	@SerialName("clouds")
	val clouds: Clouds,

	@SerialName("sys")
	val sys: Sys,

	@SerialName("dt")
	val dt: Int,

	@SerialName("coord")
	val coord: Coord,

	@SerialName("weather")
	val weather: List<WeatherItem>,

	@SerialName("name")
	val name: String,

	@SerialName("cod")
	val cod: Int,

	@SerialName("id")
	val id: Int,

	@SerialName("base")
	val base: String,

	@SerialName("wind")
	val wind: Wind
)

@Serializable
data class Main(

	@SerialName("temp")
	val temp: Double,

	@SerialName("temp_min")
	val tempMin: Double,

	@SerialName("grnd_level")
	val grndLevel: Int,

	@SerialName("humidity")
	val humidity: Int,

	@SerialName("pressure")
	val pressure: Int,

	@SerialName("sea_level")
	val seaLevel: Int,

	@SerialName("feels_like")
	val feelsLike: Double,

	@SerialName("temp_max")
	val tempMax: Double
)

@Serializable
data class Sys(

	@SerialName("country")
	val country: String,

	@SerialName("sunrise")
	val sunrise: Int,

	@SerialName("sunset")
	val sunset: Int
)

@Serializable
data class Coord(

	@SerialName("lon")
	val lon: Double,

	@SerialName("lat")
	val lat: Double
)

@Serializable
data class Clouds(

	@SerialName("all")
	val all: Int
)

@Serializable
data class WeatherItem(

	@SerialName("icon")
	val icon: String,

	@SerialName("description")
	val description: String,

	@SerialName("main")
	val main: String,

	@SerialName("id")
	val id: Int
)

@Serializable
data class Wind(

	@SerialName("deg")
	val deg: Int,

	@SerialName("speed")
	val speed: Double,

	@SerialName("gust")
	val gust: Double
)
