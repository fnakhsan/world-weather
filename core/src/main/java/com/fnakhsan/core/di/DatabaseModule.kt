package com.fnakhsan.core.di

import android.content.Context
import androidx.room.Room
import com.fnakhsan.core.data.source.database.WeatherDao
import com.fnakhsan.core.data.source.database.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather.db"
        ).build()
//      .createFromAsset("database/dummy.db")
    }

    @Provides
    fun provideAnimeDao(database: WeatherDatabase): WeatherDao = database.weatherDao()
}