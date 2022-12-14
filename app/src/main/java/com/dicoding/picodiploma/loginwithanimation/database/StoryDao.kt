package com.dicoding.picodiploma.loginwithanimation.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.picodiploma.loginwithanimation.local.LocalStories

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<LocalStories>)

    @Query("SELECT * FROM stories")
    fun getAllStory() : PagingSource<Int, LocalStories>

    @Query("DELETE from stories")
    suspend fun deleteAll()
}