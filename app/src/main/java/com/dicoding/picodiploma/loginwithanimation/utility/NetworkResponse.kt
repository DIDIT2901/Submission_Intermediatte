package com.dicoding.picodiploma.loginwithanimation.utility

sealed class NetworkResponse<out R> {
    data class Success<out T>(val data: T) : NetworkResponse<T>()
    data class Error(val error: String) : NetworkResponse<Nothing>()
    object Loading : NetworkResponse<Nothing>()
}
