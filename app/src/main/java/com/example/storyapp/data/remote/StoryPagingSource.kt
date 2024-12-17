package com.example.storyapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.data.model.Story
import retrofit2.HttpException
import java.io.IOException

class StoryPagingSource(
    private val apiInterface: ApiInterface,
    private val token: String
) : PagingSource<Int, Story>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val response = apiInterface.getStories("Bearer $token", page, params.loadSize)
            val stories = response.body()?.listStory ?: emptyList()

            LoadResult.Page(
                data = stories,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (stories.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition
    }
}
