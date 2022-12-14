package com.dicoding.picodiploma.loginwithanimation

import androidx.lifecycle.LiveData
import com.dicoding.picodiploma.loginwithanimation.api.*
import com.dicoding.picodiploma.loginwithanimation.local.LocalStories
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.view.main.StoryModel

object DummyData {
    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(
            error = false,
            message = "Login Success",
            loginResult = generateDummyUserResponse()
        )
    }

    fun generateDummySignUpResponse(): SignUpResponse {
        return SignUpResponse(
            error = false,
            message = "Success Sign Up"
        )
    }

    fun generateDummyUserResponse(): UserResponse {
        return UserResponse(
            userId = "A07",
            name = "Imawan",
            token = "abcde12345"
        )
    }

    fun generateDummyUserModel(): UserModel {
        return UserModel(
            id = "A07",
            name = "M. Didit Imawan Pramono",
            email = "diditimawan10@gmail.com",
            token = "abcde12345",
            isLogin = true
        )
    }

    fun generateDummyStoryResponse(): List<LocalStories> {
        val items: MutableList<LocalStories> = arrayListOf()
        for (i in 0..100) {
            val storyList = LocalStories(
                i.toString(),
                "user + $i",
                "description + $i",
                "photo + $i",
                "created at + $i"
            )
            items.add(storyList)
        }
        return items
    }

    fun generateSummyGetStoryResponse(): GetAllStoriesResponse {
        return GetAllStoriesResponse(
            error = false,
            message = "Success",
            stories = generateDummyStoryModel()
        )
    }

    private fun generateDummyStoryModel(): MutableList<StoryModel> {
        val items: MutableList<StoryModel> = arrayListOf()
        for (i in 0..100){
            val storyList = StoryModel(
                id = i.toString(),
                name = "user + $i",
                description = "description + $i",
                photoUrl = "photo + $i",
                createdAt = "created at + $i",
                lat = i.toDouble(),
                lon = i.toDouble()
            )
            items.add(storyList)
        }
        return items
    }

    fun generateDummyResponse(): StoryUploadResponse {
        return StoryUploadResponse(
            error = false,
            message = "Success Upload Story",
        )
    }
}