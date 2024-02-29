package com.fnakhsan.core.data.repository

import android.content.Context
import android.util.Log
import com.fnakhsan.core.data.base.DataResource
import com.fnakhsan.core.data.mapper.mapEntitiesToModel
import com.fnakhsan.core.data.mapper.mapModelToEntities
import com.fnakhsan.core.data.mapper.mapResponsesToEntities
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
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
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
            override fun loadFromDB(data: WeatherResponse?): Flow<WeatherModel?> {
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
            override fun loadFromDB(data: WeatherResponse?): Flow<WeatherModel?> {
                Log.d("mapper", "loadDb $lat, $lon")
                return if (data != null) localDataSource.getLocation(data.name!!).map {
                    Log.d("mapper", it.toString())
                    mapEntitiesToModel(it)
                } else localDataSource.getLocation( getLocation() ?: "").map {
                    mapEntitiesToModel(it)
                }
            }

            override fun shouldFetch(data: WeatherModel?): Boolean = isNetworkAvailable(context)

            override suspend fun createCall(): Flow<DataResource<WeatherResponse>> {
                return remoteDataSource.searchWeather(lat, lon)
            }

            override suspend fun saveCallResult(data: WeatherResponse) {
                weatherDatastore.saveLocation(data.name.toString())
                val weather = mapResponsesToEntities(data)
                Log.d("mapper", "saveDb $weather")
                localDataSource.upsertFavoriteLocation(weather)
                weatherCurrLocation = mapEntitiesToModel(weather)
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

    override fun getFavListWeather(): Flow<DataResource<List<WeatherModel>>> = channelFlow {
        Log.d("fav", "masuk")
        val listWeather = mutableListOf<WeatherModel>()
        weatherCurrLocation?.let { listWeather.add(it) }
        Log.d("fav", "masuk $listWeather")
        localDataSource.getListFavoriteLocation().collectLatest {
            it.map { item ->
                val locations = setOf(
                    "New York",
                    "Singapore",
                    "Mumbai",
                    "Delhi",
                    "Sydney",
                    "Melbourne",
                    item?.location
                )
                locations.map { location ->
                    if (location != null) {
                        Log.d("fav", "masuk lokasi")
                        searchWeather(location).collectLatest { search ->
                            when (search) {
                                is DataResource.Success -> {
                                    Log.d("fav", "masuk ${search.data}")
//                                    listWeather.add(search.data)
                                }

                                is DataResource.Error ->
                                    send(DataResource.Error(search.exception, search.errorCode))

                                is DataResource.Loading -> send(DataResource.Loading)
                            }
                        }
                    }
                }
            }
        }
        send(DataResource.Success(listWeather))
    }

    override suspend fun setFavWeather(weatherModel: WeatherModel) {
        appExecutors.diskIO()
            .execute {
                localDataSource.upsertFavoriteLocation(
                    mapModelToEntities(weatherModel)
                )
            }
    }

    override fun isFavWeather(id: Int): Flow<Boolean> {
        TODO("Not yet implemented")
    }
}