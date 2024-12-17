package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.remote.ApiClient
import com.example.storyapp.data.remote.StoryPagingSource
import kotlinx.coroutines.flow.Flow

class MainViewModel : ViewModel() {

    private var currentPagingFlow: Flow<PagingData<Story>>? = null

    // Get the paged stories from the API
    fun getPagedStories(token: String): Flow<PagingData<Story>> {
        currentPagingFlow?.let { return it }

        val flow = Pager(
            config = PagingConfig(
                pageSize = 3,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(ApiClient.apiInterface, token) }
        ).flow.cachedIn(viewModelScope)

        currentPagingFlow = flow
        return flow
    }
}