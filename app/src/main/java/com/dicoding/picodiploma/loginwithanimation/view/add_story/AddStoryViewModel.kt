package com.dicoding.picodiploma.loginwithanimation.view.add_story

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.repository.Repository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: Repository) : ViewModel() {
    fun uploadStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody
    ) = repository.uploadStory(token, photo, description)
}