package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.api.UserResponse
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.repository.Repository
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference, private val repository: Repository) : ViewModel() {
    fun saveUser(user: UserResponse) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun loginUser(email: String, password: String) = repository.loginUser(email, password)
}