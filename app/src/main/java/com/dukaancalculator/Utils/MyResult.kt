package com.dukaancalculator.Utils


sealed class MyResult<out T : Any> {

    data class Success<out T : Any>(val data: T) : MyResult<T>()
    data class Error(val msg: String) : MyResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$msg]"
        }
    }

    fun whenSuccess(onSuccess: (T) -> Unit) {
        if (this is Success) {
            onSuccess(data)
        }
    }

    fun whenError(onError: (Exception) -> Unit) {
        if (this is Error) {
            onError(Exception(msg))
        }
    }

}