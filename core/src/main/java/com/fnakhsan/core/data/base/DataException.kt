package com.fnakhsan.core.data.base

import android.content.Context
import com.fnakhsan.core.utils.UiText
import com.fnakhsan.core.utils.UiText.Companion.asString

open class DataException(context: Context, message: UiText? = null, cause: Throwable? = null) :
    Throwable(message?.asString(context), cause)

class NetworkException(context: Context, message: UiText? = null, cause: Throwable? = null) :
    DataException(context, message, cause)

class ServerException(context: Context, message: UiText? = null, cause: Throwable? = null) :
    DataException(context, message, cause)

class ClientException(context: Context, message: UiText? = null, cause: Throwable? = null) :
    DataException(context, message, cause)

class UnknownException(context: Context, message: UiText? = null, cause: Throwable? = null) :
    DataException(context, message, cause)
