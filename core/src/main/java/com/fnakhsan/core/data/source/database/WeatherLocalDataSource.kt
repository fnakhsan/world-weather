package com.fnakhsan.core.data.source.database

import androidx.sqlite.db.SimpleSQLiteQuery
import com.fnakhsan.core.data.model.weather.WeatherEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherLocalDataSource @Inject constructor(private val weatherDao: WeatherDao) {

    fun getListFavoriteLocation(): Flow<List<WeatherEntity?>> = weatherDao.getListFavoriteLocation()

    private val nameQuery = "SELECT * FROM weather WHERE location LIKE"
    fun getLocation(query: String): Flow<WeatherEntity> =
        weatherDao.getLocation(SimpleSQLiteQuery("$nameQuery '$query'"))

    fun getLocation(lat: Double, lon: Double): Flow<WeatherEntity> =
        weatherDao.getLocation(SimpleSQLiteQuery("SELECT * FROM weather WHERE latitude LIKE '$lat' AND longitude LIKE '$lon'"))

    fun upsertFavoriteLocation(weatherEntity: WeatherEntity) =
        weatherDao.upsertFavoriteLocation(weatherEntity)

    fun isFavoriteLocation(id: Int): Flow<Boolean> =
        weatherDao.isFavoriteLocation(id)
}