package com.example.vitsi.domain

sealed class TheResult<out R> {

    data class Success<out T>(val data: T) : TheResult<T>()
    data class Error(val exception: Exception) : TheResult<Nothing>()
    object Loading : TheResult<Nothing>()

    fun tryData() = (this as? Success)?.data
    fun forceData() = (this as Success).data

    fun error() = (this as? Error)?.exception

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }

    companion object {
        fun <T> theSuccess(data: T) = Success(data)
        fun theError(exception: Exception) = Error(exception)
    }
}

val TheResult<*>.succeeded
    get() = this is TheResult.Success && data != null