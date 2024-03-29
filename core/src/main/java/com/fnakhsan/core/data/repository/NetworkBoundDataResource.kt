package com.fnakhsan.core.data.repository

import com.fnakhsan.core.data.base.DataResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

abstract class NetworkBoundDataResource<ResultType, RequestType> {

    private var result: Flow<DataResource<ResultType>> = flow {
        emit(DataResource.Loading)
        if (shouldFetch()) {
            emit(DataResource.Loading)
            when (val apiResponse = createCall().first()) {
                is DataResource.Success -> {
                    saveCallResult(apiResponse.data)
                    emitAll(loadFromDB().map {
                        DataResource.Success(it)
                    })
                }

                is DataResource.Error -> {
                    onFetchFailed()
                    emit(DataResource.Error(apiResponse.exception, apiResponse.errorCode))
                }

                is DataResource.Loading -> {
                    emit(DataResource.Loading)
                }
            }
        } else {
            emitAll(loadFromDB().map { DataResource.Success(it) })
        }
    }

    protected open fun onFetchFailed() {}

    protected abstract fun loadFromDB(): Flow<ResultType>

    protected abstract fun shouldFetch(): Boolean

    protected abstract suspend fun createCall(): Flow<DataResource<RequestType>>

    protected abstract suspend fun saveCallResult(data: RequestType)

    fun asFlow(): Flow<DataResource<ResultType>> = result
}