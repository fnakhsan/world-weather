package com.fnakhsan.core.data.model.weather

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo
    val id: Int,
    @ColumnInfo
    val location: String,
    @ColumnInfo
    val latitude: Double,
    @ColumnInfo
    val longitude: Double,
    @ColumnInfo
    val iconUrl: String,
    @ColumnInfo
    val description: String,
    @ColumnInfo
    val datetime: Long,
    @ColumnInfo
    val lastUpdate: Long,
    @ColumnInfo
    val temperature: Double,
    @ColumnInfo
    val feelsLike: Double,
    @ColumnInfo
    val humidity: Int,
    @ColumnInfo
    val windSpeed: Double,
    @ColumnInfo
    val visibility: Int,
    @ColumnInfo
    val cloudiness: Int,

    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean = false,
)