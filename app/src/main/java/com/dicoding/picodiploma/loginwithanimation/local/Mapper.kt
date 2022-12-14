package com.dicoding.picodiploma.loginwithanimation.local

import com.dicoding.picodiploma.loginwithanimation.view.main.StoryModel

object Mapper {
    fun mapStory(story: StoryModel) : LocalStories {
        return LocalStories(
            id = story.id,
            name = story.name,
            createdAt = story.createdAt,
            photoUrl = story.photoUrl,
            description = story.description
        )
    }
}