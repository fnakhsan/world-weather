package com.fnakhsan.core.domain.model

data class ExceptionModel(
    val statusCode: String,
    val statusMessage: String,
) : Exception()
