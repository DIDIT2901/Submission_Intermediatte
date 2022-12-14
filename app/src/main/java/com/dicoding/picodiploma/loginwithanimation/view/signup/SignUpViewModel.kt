package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.repository.Repository

class SignUpViewModel(private val repository: Repository) : ViewModel() {
    fun signUp(
        name: String,
        email: String,
        password: String
    ) = repository.signUp(name, email, password)
}