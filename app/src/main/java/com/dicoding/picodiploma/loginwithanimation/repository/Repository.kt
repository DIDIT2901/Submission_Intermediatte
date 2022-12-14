package com.dicoding.picodiploma.loginwithanimation.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.dicoding.picodiploma.loginwithanimation.api.*
import javax.inject.Inject
import com.dicoding.picodiploma.loginwithanimation.database.StoryDatabase
import com.dicoding.picodiploma.loginwithanimation.local.LocalStories
import com.dicoding.picodiploma.loginwithanimation.network.StoryRemoteMediator
import com.dicoding.picodiploma.loginwithanimation.utility.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

@OptIn(ExperimentalPagingApi::class)
class Repository @Inject constructor(
    private val api: ApiService,
    private val storyDB: StoryDatabase
) {
    fun getAllStoriesWithLocation(token: String): LiveData<NetworkResponse<GetAllStoriesResponse>> = liveData {
        emit(NetworkResponse.Loading)
        try {
            val result = api.getAllStoriesWithLocation("Bearer $token", location = 1, sizes = 100)
            emit(NetworkResponse.Success(result))
        } catch (e: Exception) {
            emit(NetworkResponse.Error(e.message.toString()))
        }
    }

    fun getStoriesWithPagination(token : String): LiveData<PagingData<LocalStories>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(
                storyDB = storyDB,
                api = api,
                token = token
            ),
            pagingSourceFactory = {
                storyDB.storyDao().getAllStory()
            }
        ).liveData
    }

    fun loginUser(email: String, password: String) : LiveData<NetworkResponse<LoginResponse>> = liveData {
        emit(NetworkResponse.Loading)
        try {
            val result = api.login(email, password)
            emit(NetworkResponse.Success(result))
        } catch (e: Exception) {
            Log.d("StoryRepository", "Login: ${e.message.toString()} ")
            emit(NetworkResponse.Error(e.message.toString()))
        }
    }

    fun signUp(
        name: String,
        email: String,
        password: String
    ): LiveData<NetworkResponse<SignUpResponse>> = liveData {
        emit(NetworkResponse.Loading)
        try {
            val result = api.signUp(name, email, password)
            emit(NetworkResponse.Success(result))
        } catch (e: Exception) {
            Log.d("StoryRepository", "Signup: ${e.message.toString()} ")
            emit(NetworkResponse.Error(e.message.toString()))
        }
    }

    fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        description: RequestBody
    ): LiveData<NetworkResponse<StoryUploadResponse>> = liveData {
        emit(NetworkResponse.Loading)
        try {
            val result = api.doUploadStory(token, image, description)
            emit(NetworkResponse.Success(result))
        } catch (e: Exception) {
            Log.d("StoryRepository", "upStory: ${e.message.toString()} ")
            emit(NetworkResponse.Error(e.message.toString()))
        }
    }
}