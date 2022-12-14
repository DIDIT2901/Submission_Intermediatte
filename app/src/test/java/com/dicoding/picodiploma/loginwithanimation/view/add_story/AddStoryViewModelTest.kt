package com.dicoding.picodiploma.loginwithanimation.view.add_story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.picodiploma.loginwithanimation.DummyData
import com.dicoding.picodiploma.loginwithanimation.api.GetAllStoriesResponse
import com.dicoding.picodiploma.loginwithanimation.api.StoryUploadResponse
import com.dicoding.picodiploma.loginwithanimation.getOrAwaitValue
import com.dicoding.picodiploma.loginwithanimation.repository.Repository
import com.dicoding.picodiploma.loginwithanimation.utility.NetworkResponse
import com.dicoding.picodiploma.loginwithanimation.view.main.StoryModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var mockRepo: Repository
    private lateinit var addStoryViewModel: AddStoryViewModel

    @Before
    fun setUp() {
        addStoryViewModel = AddStoryViewModel(mockRepo)
    }

    @Test
    fun `when upload story is successful and not null`() {
        val file = Mockito.mock(File::class.java)
        val description = "walelaje".toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
        val expectedPostStory = MutableLiveData<NetworkResponse<StoryUploadResponse>>()
        expectedPostStory.value = NetworkResponse.Success(DummyData.generateDummyResponse())
        Mockito.`when`(mockRepo.uploadStory("abcde12345", imageMultipart, description)).thenReturn(expectedPostStory)

        val actualPostStory = addStoryViewModel.uploadStory("abcde12345", imageMultipart, description).getOrAwaitValue()

        Mockito.verify(mockRepo).uploadStory("abcde12345", imageMultipart, description)
        Assert.assertTrue(actualPostStory is NetworkResponse.Success)
        Assert.assertNotNull(actualPostStory)
    }
}