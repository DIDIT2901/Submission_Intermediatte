package com.dicoding.picodiploma.loginwithanimation.model

data class UserModel(
    val id: String,
    val name: String,
    val email: String,
    val token: String,
    val isLogin: Boolean
)