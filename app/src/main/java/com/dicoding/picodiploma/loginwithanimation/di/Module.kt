package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.database.StoryDatabase
import com.dicoding.picodiploma.loginwithanimation.repository.Repository

object Module {
    fun provideRepository(context: Context) : Repository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig().getApiService()
        return Repository(apiService, database)
    }
}