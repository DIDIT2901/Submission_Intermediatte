package com.dicoding.picodiploma.loginwithanimation.network

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.picodiploma.loginwithanimation.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.api.GetAllStoriesResponse
import com.dicoding.picodiploma.loginwithanimation.database.StoryDatabase
import com.dicoding.picodiploma.loginwithanimation.local.LocalStories
import com.dicoding.picodiploma.loginwithanimation.local.Mapper
import com.dicoding.picodiploma.loginwithanimation.local.RemoteKeys

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator (
    private val storyDB : StoryDatabase,
    private val api: ApiService,
    private val token: String
        ) : RemoteMediator<Int, LocalStories>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocalStories>
    ): MediatorResult {
        val page = when(loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData : GetAllStoriesResponse = api.getStoriesWithPage(
                token = token,
                page = page,
                size = state.config.pageSize
            )
            val endOfPaginationReached = responseData.stories.isEmpty()

            storyDB.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storyDB.keyDao().deleteRemoteKeys()
                    storyDB.storyDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.stories.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                storyDB.keyDao().insertAll(keys)

                val localStories = arrayListOf<LocalStories>()

                responseData.stories.forEach {
                    localStories.add(Mapper.mapStory(it))
                }

                storyDB.storyDao().insertStory(localStories as List<LocalStories>)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }

    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, LocalStories>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            storyDB.keyDao().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, LocalStories>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            storyDB.keyDao().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, LocalStories>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDB.keyDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}