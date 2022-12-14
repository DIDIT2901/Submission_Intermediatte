package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.picodiploma.loginwithanimation.DummyData
import com.dicoding.picodiploma.loginwithanimation.MainDispatcherRule
import com.dicoding.picodiploma.loginwithanimation.api.GetAllStoriesResponse
import com.dicoding.picodiploma.loginwithanimation.getOrAwaitValue
import com.dicoding.picodiploma.loginwithanimation.local.LocalStories
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.repository.Repository
import com.dicoding.picodiploma.loginwithanimation.utility.NetworkResponse
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mainViewModel: MainViewModel
    @Mock private val mockStory = Mockito.mock(Repository::class.java)
    @Mock private val mockPref = Mockito.mock(UserPreference::class.java)

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(mockPref, mockStory)
    }

    @Test
    fun `when getUser() called return success and not null`() = runTest {
        val expectedResponse = flowOf(DummyData.generateDummyUserModel())
        Mockito.`when`(mockPref.getUser()).thenReturn(expectedResponse)

        mainViewModel.getUser().collect {
            Assert.assertNotNull(it.token)
            assertEquals(DummyData.generateDummyUserModel().token, it.token)
        }

        Mockito.verify(mockPref).getUser()
    }

    @Test
    fun `when Get Story Should Not Null & check data availability`() = runTest {
        val dummyStory = DummyData.generateDummyStoryResponse()
        val data: PagingData<LocalStories> = StoryPagedTestSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<LocalStories>>()
        expectedStory.value = data

        Mockito.`when`(mockStory.getStoriesWithPagination("abcde12345")).thenReturn(expectedStory)

        val actualStory: PagingData<LocalStories> =
            mainViewModel.getStories("abcde12345").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
    }

    @Test
    fun `when Logout success`() = runTest {
        mainViewModel.logout()
        Mockito.verify(mockPref).logout()
    }

    @Test
    fun `when GetSoriesWithLocation return success and not null`() = runTest {
        val expectedStory = MutableLiveData<NetworkResponse<GetAllStoriesResponse>>()
        expectedStory.value = NetworkResponse.Success(DummyData.generateSummyGetStoryResponse())
        Mockito.`when`(mockStory.getAllStoriesWithLocation("abcde12345")).thenReturn(expectedStory)

        val actualStory = mainViewModel.getStoriesWithLocation("abcde12345").getOrAwaitValue()
        Mockito.verify(mockStory).getAllStoriesWithLocation("abcde12345")

        Assert.assertTrue(actualStory is NetworkResponse.Success)
        Assert.assertNotNull(actualStory)
    }
}

class StoryPagedTestSource : PagingSource<Int, LiveData<List<LocalStories>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<LocalStories>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<LocalStories>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object {
        fun snapshot(items: List<LocalStories>): PagingData<LocalStories> {
            return PagingData.from(items)
        }
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}