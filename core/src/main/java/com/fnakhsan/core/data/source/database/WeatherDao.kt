package com.fnakhsan.core.data.source.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.fnakhsan.core.data.model.weather.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather where weather.is_favorite = 1 ORDER BY weather.is_user_location DESC")
    fun getListFavoriteLocation(): Flow<List<WeatherEntity>>

    @RawQuery(observedEntities = [WeatherEntity::class])
    fun getLocation(query: SupportSQLiteQuery): Flow<WeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertFavoriteLocation(weatherEntity: WeatherEntity)

    @Query("SELECT EXISTS(SELECT * FROM weather WHERE weather.id = :id AND weather.is_favorite = 1 )")
    fun isFavoriteLocation(id: Int): Flow<Boolean>
}