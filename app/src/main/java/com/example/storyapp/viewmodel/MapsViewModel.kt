package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.remote.ApiClient

class MapsViewModel : ViewModel() {

    fun getStoriesWithLocation(token: String): LiveData<List<Story>> = liveData {
        try {
            val response = ApiClient.apiInterface.getStoriesWithLocation("Bearer $token")
            if (response.isSuccessful) {
                emit(response.body()?.listStory?.filter { it.lat != null && it.lon != null } ?: emptyList())
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
