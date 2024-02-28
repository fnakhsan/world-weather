package com.fnakhsan.core.data.source.database

import androidx.sqlite.db.SimpleSQLiteQuery
import com.fnakhsan.core.data.model.weather.WeatherEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherLocalDataSource @Inject constructor(private val weatherDao: WeatherDao) {

    fun getListFavoriteLocation(): Flow<List<WeatherEntity>> = weatherDao.getListFavoriteLocation()

    private val simpleSQLiteQuery = "SELECT * FROM weather WHERE location LIKE"
    fun getLocation(query: String): Flow<WeatherEntity> =
        weatherDao.getLocation(SimpleSQLiteQuery("$simpleSQLiteQuery '$query' LIMIT 1"))

    fun upsertFavoriteLocation(weatherEntity: WeatherEntity) =
        weatherDao.upsertFavoriteLocation(weatherEntity)

    fun isFavoriteLocation(id: Int): Flow<Boolean> =
        weatherDao.isFavoriteLocation(id)
}