package com.fnakhsan.core.data.source.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fnakhsan.core.data.model.weather.WeatherEntity

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

}