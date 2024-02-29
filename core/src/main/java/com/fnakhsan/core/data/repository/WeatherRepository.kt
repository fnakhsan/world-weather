package com.fnakhsan.core.data.repository

import android.content.Context
import android.util.Log
import com.fnakhsan.core.data.base.DataResource
import com.fnakhsan.core.data.base.asDataResourceFlow
import com.fnakhsan.core.data.mapper.mapEntitiesToModel
import com.fnakhsan.core.data.mapper.mapModelToFavEntities
import com.fnakhsan.core.data.mapper.mapResponsesToEntities
import com.fnakhsan.core.data.mapper.mapResponsesToFavEntities
import com.fnakhsan.core.data.model.weather.WeatherResponse
import com.fnakhsan.core.data.source.database.WeatherLocalDataSource
import com.fnakhsan.core.data.source.datastore.WeatherDatastore
import com.fnakhsan.core.data.source.network.WeatherRemoteDataSource
import com.fnakhsan.core.domain.model.WeatherModel
import com.fnakhsan.core.domain.usecase.weather.IWeatherRepository
import com.fnakhsan.core.utils.AppExecutors
import com.fnakhsan.core.utils.isNetworkAvailable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource,
    private val weatherDatastore: WeatherDatastore,
    private val appExecutors: AppExecutors
) : IWeatherRepository {
    private var weatherCurrLocation: WeatherModel? = null

    //    override fun searchWeather(query: String): Flow<DataResource<WeatherModel>> {
//        return remoteDataSource.searchWeather(query)
//    }
//
//    override fun searchWeather(lat: Double, lon: Double): Flow<DataResource<WeatherModel>> {
//        return remoteDataSource.searchWeather(lat, lon)
//    }

    fun getLocation(): String? = runBlocking {
        weatherDatastore.getLocation().first()
    }

    override fun searchWeather(query: String): Flow<DataResource<WeatherModel?>> =
        object : NetworkBoundDataResource<WeatherModel?, WeatherResponse>() {
            override fun loadFromDB(): Flow<WeatherModel?> {
                return localDataSource.getLocation(query).map {
                    mapEntitiesToModel(it)
                }
            }

            override fun shouldFetch(data: WeatherModel?): Boolean = true

            override suspend fun createCall(): Flow<DataResource<WeatherResponse>> {
                return remoteDataSource.searchWeather(query)
            }

            override suspend fun saveCallResult(data: WeatherResponse) {
                val weather = mapResponsesToEntities(data)
                localDataSource.upsertFavoriteLocation(weather)
            }
        }.asFlow()

    override fun searchWeather(lat: Double, lon: Double): Flow<DataResource<WeatherModel?>> =
        object : NetworkBoundDataResource<WeatherModel?, WeatherResponse>() {
            override fun loadFromDB(): Flow<WeatherModel?> {
                Log.d("mapper", "loadDb $lat, $lon")
                return localDataSource.getLocation(getLocation() ?: "").map {
                    mapEntitiesToModel(it)
                }
            }

            override fun shouldFetch(data: WeatherModel?): Boolean = isNetworkAvailable(context)

            override suspend fun createCall(): Flow<DataResource<WeatherResponse>> {
                return remoteDataSource.searchWeather(lat, lon)
            }

            override suspend fun saveCallResult(data: WeatherResponse) {
                weatherDatastore.saveLocation(data.name.toString())
                val weather = mapResponsesToFavEntities(data)
                Log.d("mapper", "saveDb $weather")
                weatherCurrLocation = mapEntitiesToModel(weather)
                localDataSource.upsertFavoriteLocation(weather)
            }
        }.asFlow()


//    override fun getFavListWeather(): Flow<DataResource<List<WeatherModel>>> {
//        return localDataSource.getListFavoriteLocation().map {
//            DataResource.Success(it.map { item ->
//                searchWeather(item.location)
//                mapEntitiesToModel(item)
//            })
//        }
//    }

//    suspend fun setInitFav() {
//        Log.d("fav", "masuk")
//        searchWeather("New York").collectLatest {
//            when (it) {
//                is DataResource.Success -> {
//                    if (it.data != null) {
//                        setFavWeather(it.data)
//                    }
//                }
//
//                else -> {}
//            }
//        }
//        searchWeather("Singapore").collectLatest {
//            when (it) {
//                is DataResource.Success -> {
//                    if (it.data != null) {
//                        setFavWeather(it.data)
//                    }
//                }
//
//                else -> {}
//            }
//        }
//        searchWeather("Mumbai").collectLatest {
//            when (it) {
//                is DataResource.Success -> {
//                    if (it.data != null) {
//                        setFavWeather(it.data)
//                    }
//                }
//
//                else -> {}
//            }
//        }
//        searchWeather("Delhi").collectLatest {
//            when (it) {
//                is DataResource.Success -> {
//                    if (it.data != null) {
//                        setFavWeather(it.data)
//                    }
//                }
//
//                else -> {}
//            }
//        }
//        searchWeather("Sydney").collectLatest {
//            when (it) {
//                is DataResource.Success -> {
//                    if (it.data != null) {
//                        setFavWeather(it.data)
//                    }
//                }
//
//                else -> {}
//            }
//        }
//        searchWeather("Melbourne").collectLatest {
//            when (it) {
//                is DataResource.Success -> {
//                    if (it.data != null) {
//                        setFavWeather(it.data)
//                    }
//                }
//
//                else -> {}
//            }
//        }
//        val locations = setOf(
//            "New York",
//            "Singapore",
//            "Mumbai",
//            "Delhi",
//            "Sydney",
//            "Melbourne"
//        )
//    }

    override fun getFavListWeather(): Flow<DataResource<List<WeatherModel>>> =
        localDataSource.getListFavoriteLocation().map { list ->
            list.filter {
                it?.location != getLocation()
            }.map { entity ->
                mapEntitiesToModel(entity)
            }
        }.asDataResourceFlow(context)


    override fun setFavWeather(weatherModel: WeatherModel) {
        appExecutors.diskIO()
            .execute {
                localDataSource.upsertFavoriteLocation(
                    mapModelToFavEntities(weatherModel)
                )
            }
    }

    override fun isFavWeather(id: Int): Flow<Boolean> {
        TODO("Not yet implemented")
    }
}