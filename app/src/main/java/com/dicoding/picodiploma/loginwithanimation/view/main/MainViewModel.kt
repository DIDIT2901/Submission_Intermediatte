package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import com.dicoding.picodiploma.loginwithanimation.local.LocalStories
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val pref: UserPreference,
    private val repository: Repository
) : ViewModel() {
    fun getUser(): Flow<UserModel> {
        return pref.getUser()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    fun getStories(token: String): LiveData<PagingData<LocalStories>> {
        return repository.getStoriesWithPagination(token)
    }

    fun getStoriesWithLocation(token: String) = repository.getAllStoriesWithLocation(token)

}