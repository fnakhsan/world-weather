package com.fnakhsan.core.data.base

sealed class DataResource<out R> private constructor() {
    data class Success<out T>(val data: T) : DataResource<T>()
    data class Error(val exception: DataException, val errorCode: String? = null) : DataResource<Nothing>()
    data object Loading : DataResource<Nothing>()
}