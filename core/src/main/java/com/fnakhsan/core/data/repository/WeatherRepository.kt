package com.fnakhsan.core.data.repository

import com.fnakhsan.core.data.base.DataResource
import com.fnakhsan.core.data.mapper.mapModelToEntities
import com.fnakhsan.core.data.source.database.WeatherLocalDataSource
import com.fnakhsan.core.data.source.network.WeatherRemoteDataSource
import com.fnakhsan.core.domain.model.WeatherModel
import com.fnakhsan.core.domain.usecase.weather.IWeatherRepository
import com.fnakhsan.core.utils.AppExecutors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource,
    private val appExecutors: AppExecutors
) : IWeatherRepository {
    override fun searchWeather(query: String): Flow<DataResource<WeatherModel>> {
        return remoteDataSource.searchWeather(query)
    }

    override fun searchWeather(lat: Double, lon: Double): Flow<DataResource<WeatherModel>> {
        return remoteDataSource.searchWeather(lat, lon)
    }
//    override fun searchWeather(query: String): Flow<DataResource<WeatherModel>> =
//        object : NetworkBoundDataResource<WeatherModel, WeatherResponse>() {
//            override fun loadFromDB(): Flow<WeatherModel> {
//                return localDataSource.getLocation(query).map {
//                    mapEntitiesToModel(it)
//                }
//            }
//
//            override fun shouldFetch(data: WeatherModel?): Boolean = true
//
//            override suspend fun createCall(): Flow<DataResource<WeatherResponse>> {
//                return remoteDataSource.searchWeather(query)
//            }
//
//            override suspend fun saveCallResult(data: WeatherResponse) {
//                val weather = mapResponsesToEntities(data)
//                localDataSource.upsertFavoriteLocation(weather)
//            }
//        }.asFlow()




//    override fun getFavListWeather(): Flow<DataResource<List<WeatherModel>>> {
//        return localDataSource.getListFavoriteLocation().map {
//            DataResource.Success(it.map { item ->
//                searchWeather(item.location)
//                mapEntitiesToModel(item)
//            })
//        }
//    }

    override fun getFavListWeather(): Flow<DataResource<List<WeatherModel>>> = flow {
        val listWeather = mutableListOf<WeatherModel>()
        localDataSource.getListFavoriteLocation().map {
            it.map { item ->
                searchWeather(item.location).collect { search ->
                    when (search) {
                        is DataResource.Success -> listWeather.add(search.data)
                        is DataResource.Error ->
                            emit(DataResource.Error(search.exception, search.errorCode))

                        is DataResource.Loading -> emit(DataResource.Loading)
                    }
                }
            }
            emit(DataResource.Success(listWeather))
        }
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