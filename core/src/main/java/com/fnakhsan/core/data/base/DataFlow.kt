package com.fnakhsan.core.data.base

import android.content.Context
import com.fnakhsan.core.R
import com.fnakhsan.core.utils.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import java.io.IOException

fun <T> Flow<T>.asDataResourceFlow(context: Context): Flow<DataResource<T>> {
    return this
        .map<T, DataResource<T>> {
            DataResource.Success(it)
        }
        .onStart { emit(DataResource.Loading) }
        .catch { error ->
            val exception = when (error) {
                is HttpException -> {
                    when (error.code()) {
                        in 400..499 -> {
                            ClientException(
                                context = context,
                                message = UiText.StringResource(R.string.client_error),
                                cause = error,
                            )
                        }

                        in 500..599 -> {
                            ServerException(
                                context = context,
                                message = UiText.StringResource(R.string.server_error),
                                cause = error
                            )
                        }

                        else -> {
                            UnknownException(
                                context = context,
                                message = UiText.StringResource(R.string.http_unknown_error),
                                cause = error
                            )
                        }
                    }
                }

                is IOException -> NetworkException(
                    context = context,
                    message = UiText.StringResource(R.string.network_error),
                    cause = error
                )

                else -> DataException(
                    context = context,
                    message = UiText.StringResource(R.string.unknown_error),
                    cause = error
                )
            }

            val errorCode = when (error) {
                is HttpException -> {
                    when (error.code()) {
                        in 400..499 -> {
                            "#ER${error.code()}"
                        }

                        in 500..599 -> {
                            "#ER${error.code()}"
                        }

                        else -> {
                            "#ER${error.code()}"
                        }
                    }
                }

                else -> {
                    error.cause?.message.toString()
                }
            }
            emit(DataResource.Error(exception, errorCode))
        }
}